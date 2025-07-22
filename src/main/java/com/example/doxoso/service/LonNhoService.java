package com.example.doxoso.service;

import com.example.doxoso.model.DoiChieuKetQuaDto;
import com.example.doxoso.model.KetQua;
import com.example.doxoso.repository.KetQuaMienBacRepository;
import com.example.doxoso.repository.KetQuaMienNamRepository;
import com.example.doxoso.repository.KetQuaMienTrungRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.Normalizer;
import java.time.LocalDate;
import java.util.Optional;

@Service
public class LonNhoService {

    @Autowired
    private KetQuaMienBacRepository mbRepo;

    @Autowired
    private KetQuaMienTrungRepository mtRepo;

    @Autowired
    private KetQuaMienNamRepository mnRepo;

    // Kiểm tra hợp lệ cách đánh LỚN hoặc NHỎ
    public boolean isValid(String cachDanh) {
        if (cachDanh == null) return false;
        String cd = removeDiacritics(cachDanh).toUpperCase().trim();
        return cd.equals("LON") || cd.equals("NHO");
    }

    public void xuLyLonNho(DoiChieuKetQuaDto dto) {
        String soDanh = chuanHoa(dto.getSoDanh());      // LON hoặc NHO
        String mien = chuanHoa(dto.getMien());          // MIENBAC / MIENTRUNG / MIENNAM
        String tenDai = chuanHoa(dto.getTenDai());      // tên đài (chỉ dùng cho MT & MN)
        LocalDate ngay = dto.getNgay();

        Optional<KetQua> ketQuaDB = Optional.empty();

        // Xử lý từng miền riêng
        if (mien.equals("MIENBAC")) {
            ketQuaDB = mbRepo.findAllByNgay(ngay).stream()
                    .filter(kq -> "DB".equalsIgnoreCase(kq.getGiai()))
                    .findFirst();

        } else if (mien.equals("MIENTRUNG")) {
            ketQuaDB = mtRepo.findAllByNgay(ngay).stream()
                    .filter(kq -> "DB".equalsIgnoreCase(kq.getGiai()))
                    .findFirst();

        } else if (mien.equals("MIENNAM")) {
            ketQuaDB = mnRepo.findAllByNgay(ngay).stream()
                    .filter(kq -> "DB".equalsIgnoreCase(kq.getGiai()))
                    .findFirst();
        }

        if (ketQuaDB.isEmpty()) {
            dto.setTrung(false);
            dto.setLyDo("Không tìm thấy kết quả giải Đặc Biệt cho " + tenDai);
            return;
        }

        String giaiDB = ketQuaDB.get().getKetqua();
        if (giaiDB.length() < 2) {
            dto.setTrung(false);
            dto.setLyDo("Giải đặc biệt không hợp lệ: " + giaiDB);
            return;
        }

        String haiSoCuoiStr = giaiDB.substring(giaiDB.length() - 2);
        int haiSoCuoi;
        try {
            haiSoCuoi = Integer.parseInt(haiSoCuoiStr);
        } catch (NumberFormatException e) {
            dto.setTrung(false);
            dto.setLyDo("2 số cuối GĐB không hợp lệ: " + haiSoCuoiStr);
            return;
        }

        boolean trung = false;
        if (soDanh.equals("LON") && haiSoCuoi > 50) {
            trung = true;
        } else if (soDanh.equals("NHO") && haiSoCuoi <= 50) {
            trung = true;
        }

        dto.setTrung(trung);
        dto.setGiaiTrung("Giải Đặc Biệt");
        dto.setCachTrung("2 số cuối GĐB là " + haiSoCuoi + " → " + (trung ? "Trúng " + soDanh : "Không trúng"));
        dto.setTienTrung(trung ? dto.getTienDanh() * 2 : 0);
        if (!trung) {
            dto.setLyDo("2 số cuối GĐB là " + haiSoCuoi + " không phù hợp với cách đánh " + soDanh);
        }
    }

    // Chuẩn hoá chữ không dấu và viết hoa
    private String chuanHoa(String str) {
        if (str == null) return "";
        return removeDiacritics(str).toUpperCase().trim().replaceAll("\\s+", "");
    }

    // Bỏ dấu tiếng Việt
    private String removeDiacritics(String input) {
        if (input == null) return "";
        String normalized = Normalizer.normalize(input, Normalizer.Form.NFD);
        return normalized.replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
    }
}
