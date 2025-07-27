package com.example.doxoso.service;

import com.example.doxoso.model.DoiChieuKetQuaDto;
import com.example.doxoso.repository.KetQuaMienBacRepository;
import com.example.doxoso.repository.KetQuaMienNamRepository;
import com.example.doxoso.repository.KetQuaMienTrungRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.Normalizer;
import java.time.LocalDate;
import java.util.List;
import java.util.regex.Pattern;

@Service
public class NhoService {

    @Autowired private KetQuaMienBacRepository bacRepo;
    @Autowired private KetQuaMienTrungRepository trungRepo;
    @Autowired private KetQuaMienNamRepository namRepo;
    @Autowired private TinhTienService tinhTienService;

    public DoiChieuKetQuaDto xuLyNho(String soDanh, String mien, String tenDai, LocalDate ngay, String tienDanh) {
        DoiChieuKetQuaDto dto = new DoiChieuKetQuaDto();
        dto.setSoDanh(soDanh);
        dto.setMien(mien);
        dto.setTenDai(tenDai);
        dto.setNgay(ngay);
        dto.setCachDanh("NHỎ");
        dto.setTienDanh(tienDanh);

        if (tienDanh == null || tienDanh.isBlank()) {
            dto.setTrung(false);
            dto.setTienTrung(0);
            dto.setSaiLyDo(List.of("Thiếu tiền đánh"));
            return dto;
        }

        var ketQua = timKetQuaTheoMien(mien, tenDai, ngay);
        if (ketQua == null || !ketQua.matches("\\d{6}")) {
            dto.setTrung(false);
            dto.setTienTrung(0);
            dto.setSaiLyDo(List.of("Giải ĐB không hợp lệ: " + ketQua));
            return dto;
        }

        int so = Integer.parseInt(ketQua.substring(ketQua.length() - 2));

        boolean trungNho = so >= 0 && so <= 49;

        dto.setTrung(trungNho);
        if (trungNho) {
            dto.setGiaiTrung("Giải ĐB");
            dto.setCachTrung("Trúng NHỎ – Đuôi: " + so);
            dto.setTienTrung(tinhTienService.tinhTienTrung("NHO", tienDanh, mien));
        } else {
            dto.setTienTrung(0);
            dto.setSaiLyDo(List.of("Trật – Đuôi là " + so));
        }

        return dto;
    }

    private String timKetQuaTheoMien(String mien, String tenDai, LocalDate ngay) {
        String m = removeDiacritics(mien).toUpperCase();
        if (m.contains("BAC")) {
            return bacRepo.findAllByNgay(ngay).stream()
                    .filter(k -> k.getTenDai().equalsIgnoreCase(tenDai) && k.getGiai().equalsIgnoreCase("ĐẶC BIỆT"))
                    .map(k -> k.getSoTrung())
                    .findFirst().orElse(null);
        } else if (m.contains("TRUNG")) {
            return trungRepo.findAllByNgay(ngay).stream()
                    .filter(k -> k.getTenDai().equalsIgnoreCase(tenDai) && k.getGiai().equalsIgnoreCase("ĐẶC BIỆT"))
                    .map(k -> k.getSoTrung())
                    .findFirst().orElse(null);
        } else if (m.contains("NAM")) {
            return namRepo.findAllByNgay(ngay).stream()
                    .filter(k -> k.getTenDai().equalsIgnoreCase(tenDai) && k.getGiai().equalsIgnoreCase("ĐẶC BIỆT"))
                    .map(k -> k.getSoTrung())
                    .findFirst().orElse(null);
        }
        return null;
    }

    private String removeDiacritics(String input) {
        String normalized = Normalizer.normalize(input, Normalizer.Form.NFD);
        return Pattern.compile("\\p{InCombiningDiacriticalMarks}+")
                .matcher(normalized)
                .replaceAll("")
                .replace('đ', 'd')
                .replace('Đ', 'D');
    }
}
