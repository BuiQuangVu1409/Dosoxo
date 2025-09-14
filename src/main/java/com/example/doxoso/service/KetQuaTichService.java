// com.example.doxoso.service.KetQuaTichService.java
package com.example.doxoso.service;

import com.example.doxoso.model.*;
import com.example.doxoso.repository.KetQuaTichRepository;
import com.example.doxoso.repository.PlayerRepository;
import com.example.doxoso.repository.SoNguoiChoiRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.text.Normalizer;
import java.time.LocalDate;
import java.util.*;

@Service
@RequiredArgsConstructor
public class KetQuaTichService {

    private final KetQuaTichRepository ketQuaTichRepo;
    private final SoNguoiChoiRepository soNguoiChoiRepository;
    private final PlayerRepository playerRepository;

    // nguồn số liệu đã có sẵn
    private final TongTienTrungService tongTienTrungService;                 // tổng TRÚNG theo miền
    private final TongHopHoaHongLonNhoService tongHopHoaHongLonNhoService;   // HH + LN + tổng cộng
    // Giữ lại nếu nơi khác còn dùng, còn tại đây ta tự tính An/Thua:
    private final TongTienAnThuaMienService tongTienAnThuaMienService;       // (không còn dùng để set tienAnThua)

    @Transactional
    public List<KetQuaTich> runAndSaveForPlayer(Long playerId, String playerName, LocalDate ngay) {
        // ===== (1) Tổng TRÚNG -> map theo CODE (MB/MT/MN), tránh lệch key
        Map<String, BigDecimal> tienTrungByCode = new HashMap<>();
        TongTienTrungDto trung = tongTienTrungService.tongHopTuDb(playerId, ngay);
        if (trung != null && trung.getCacMien() != null) {
            for (TongTienTrungDto.MienDto m : trung.getCacMien()) {
                String code = toCode(s(m.getMien())); // "MIỀN BẮC" | "MB" -> "MB"
                tienTrungByCode.merge(code, bd(m.getTongTienMien()), BigDecimal::add);
            }
        }

        // ===== (2) HH + LN + (HH+LN) theo CODE (MB/MT/MN)
        TongHopHoaHongLonNhoDto hhln = tongHopHoaHongLonNhoService.tongHopMotNgay(playerId, playerName, ngay);

        Map<String, BigDecimal> hhBy = new HashMap<>();
        Map<String, BigDecimal> lnBy = new HashMap<>();
        Map<String, BigDecimal> hhCongLnBy = new HashMap<>();
        if (hhln != null) {
            hhBy.put("MB", bd(hhln.getTongDaNhanHoaHongMB()));
            hhBy.put("MT", bd(hhln.getTongDaNhanHoaHongMT()));
            hhBy.put("MN", bd(hhln.getTongDaNhanHoaHongMN()));

            lnBy.put("MB", bd(hhln.getTienLonNhoMB()));
            lnBy.put("MT", bd(hhln.getTienLonNhoMT()));
            lnBy.put("MN", bd(hhln.getTienLonNhoMN()));

            hhCongLnBy.put("MB", bd(hhln.getTongCongMB()));
            hhCongLnBy.put("MT", bd(hhln.getTongCongMT()));
            hhCongLnBy.put("MN", bd(hhln.getTongCongMN()));
        }

        // ===== (3) KHÔNG gọi service ăn/thua nữa — tự tính theo công thức chuẩn

        // ===== (4) Cộng TIỀN ĐÁNH theo CODE (từ sổ người chơi)
        Map<String, BigDecimal> tienDanhByCode = new HashMap<>();
        BigDecimal mb = BigDecimal.ZERO, mt = BigDecimal.ZERO, mn = BigDecimal.ZERO;
        List<SoNguoiChoi> soList = soNguoiChoiRepository.findByPlayer_IdAndNgay(playerId, ngay);
        for (var so : soList) {
            BigDecimal stake = parseTienDanh(so.getTienDanh()); // String -> BigDecimal
            String code = toCode(so.getMien());                 // MB/MT/MN
            if ("MB".equals(code)) mb = mb.add(stake);
            else if ("MT".equals(code)) mt = mt.add(stake);
            else if ("MN".equals(code)) mn = mn.add(stake);
        }
        tienDanhByCode.put("MB", mb);
        tienDanhByCode.put("MT", mt);
        tienDanhByCode.put("MN", mn);

        // ===== (4.1) Resolve playerName nếu không truyền vào
        String resolvedName = playerName;
        if (isBlank(resolvedName) && hhln != null && !isBlank(hhln.getPlayerName())) {
            resolvedName = hhln.getPlayerName();
        }
        if (isBlank(resolvedName)) {
            resolvedName = playerRepository.findById(playerId).map(Player::getName).orElse(null);
        }
        if (isBlank(resolvedName) && !soList.isEmpty() && soList.get(0).getPlayer() != null) {
            resolvedName = soList.get(0).getPlayer().getName();
        }

        // ===== (5) UPsert: nạp sẵn snapshot cũ trong ngày → map theo mienCode
        List<KetQuaTich> existedRows = ketQuaTichRepo.findByPlayerIdAndNgay(playerId, ngay);
        Map<String, KetQuaTich> existedByCode = new HashMap<>();
        for (KetQuaTich r : existedRows) {
            if (r.getMienCode() != null) {
                existedByCode.put(r.getMienCode(), r);
            }
        }

        // ===== (6) Lắp 3 miền MB → MT → MN và upsert
        List<KetQuaTich> rows = new ArrayList<>();
        for (String code : new String[]{"MB", "MT", "MN"}) {
            String display = display(code);

            BigDecimal tienTrung  = tienTrungByCode.getOrDefault(code, BigDecimal.ZERO);
            BigDecimal tienHH     = hhBy.getOrDefault(code, BigDecimal.ZERO);
            BigDecimal tienLN     = lnBy.getOrDefault(code, BigDecimal.ZERO);
            BigDecimal tienDanh   = tienDanhByCode.getOrDefault(code, BigDecimal.ZERO);
            BigDecimal danhHH     = hhBy.getOrDefault(code, BigDecimal.ZERO);          // đang hiểu là "đã nhận HH"
            BigDecimal danhHH_LN  = hhCongLnBy.getOrDefault(code, BigDecimal.ZERO);    // "đã nhận HH + LN"

            // TỰ TÍNH ĂN/THUA ở đây
            BigDecimal tienAT = tienTrung.subtract(tienHH.add(tienLN));

            KetQuaTich entity = KetQuaTich.builder()
                    .playerId(playerId)
                    .playerName(resolvedName)
                    .ngay(ngay)
                    .mienCode(code)
                    .mienDisplay(display)
                    .tienTrung(tienTrung)
                    .tienHoaHong(tienHH)
                    .tienLonNho(tienLN)
                    .tienAnThua(tienAT)
                    .tienDanh(tienDanh)
                    .tienDanhDaNhanHoaHong(danhHH)
                    .tienDanhDaNhanHoaHongCongLonNho(danhHH_LN)
                    .build();

            KetQuaTich old = existedByCode.get(code);
            if (old != null) {
                entity.setId(old.getId());             // UPDATE thay vì INSERT
                entity.setVersion(old.getVersion());   // nếu có @Version
                entity.setCreatedAt(old.getCreatedAt());// nếu có field
            }
            rows.add(entity);
        }

        return ketQuaTichRepo.saveAll(rows);
    }

    public List<KetQuaTich> findByPlayerAndNgay(Long playerId, LocalDate ngay) {
        return ketQuaTichRepo.findByPlayerIdAndNgay(playerId, ngay);
    }

    // ------- helpers -------
    private static String s(Object x){ return x==null?"":x.toString().trim(); }
    private static boolean isBlank(String x){ return x == null || x.trim().isEmpty(); }

    private static BigDecimal bd(Object x){
        if (x == null) return BigDecimal.ZERO;
        if (x instanceof BigDecimal b) return b;
        if (x instanceof Double d) return BigDecimal.valueOf(d);
        return new BigDecimal(x.toString());
    }

    private static String toCode(String raw){
        if (raw == null) return "";
        String u = raw.trim().toUpperCase();
        if (u.startsWith("MB") || u.contains("BẮC") || u.contains("BAC")) return "MB";
        if (u.startsWith("MT") || u.contains("TRUNG"))                    return "MT";
        if (u.startsWith("MN") || u.contains("NAM"))                      return "MN";
        return u;
    }

    private static String display(String code){
        return switch (code){
            case "MB" -> "MIỀN BẮC";
            case "MT" -> "MIỀN TRUNG";
            case "MN" -> "MIỀN NAM";
            default -> code;
        };
    }

    // Parser CHỈ lấy 1 token tiền hợp lệ, giữ âm/ngoặc, tránh ghép mọi dãy số
    private static BigDecimal parseTienDanh(String s) {
        if (s == null) return BigDecimal.ZERO;

        String n = Normalizer.normalize(s, Normalizer.Form.NFKC).trim();
        if (n.isEmpty()) return BigDecimal.ZERO;

        java.util.regex.Matcher m = java.util.regex.Pattern
                .compile("[-(]?\\d{1,3}([.,\\s]\\d{3})*([.,]\\d+)?[)]?")
                .matcher(n);

        if (!m.find()) return BigDecimal.ZERO;
        String token = m.group();

        boolean negative = token.startsWith("(") || token.startsWith("-");
        token = token.replace("(", "").replace(")", "").replaceAll("\\s+", "");

        if (token.contains(".") && token.contains(",")) {
            // ví dụ: 1.234.567,89  -> 1234567.89
            token = token.replace(".", "").replace(",", ".");
        } else {
            // bỏ dấu nhóm ngàn nếu là . và theo sau là 3 chữ số
            token = token.replaceAll("\\.(?=\\d{3}(\\D|$))", "");
            // nếu mẫu giống nhóm ngàn với dấu phẩy thì bỏ phẩy, ngược lại coi phẩy là thập phân
            if (token.matches(".*\\d,\\d{3}(\\D|$).*")) {
                token = token.replace(",", "");
            } else {
                token = token.replace(",", ".");
            }
        }

        try {
            BigDecimal v = new BigDecimal(token);
            return negative ? v.negate() : v;
        } catch (NumberFormatException e) {
            return BigDecimal.ZERO;
        }
    }
}
