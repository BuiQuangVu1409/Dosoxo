package com.example.doxoso.service;

import com.example.doxoso.model.DoiChieuKetQuaDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class DauDuoiService {

    @Autowired
    private DauService dauService;

    @Autowired
    private DuoiService duoiService;

    public DoiChieuKetQuaDto xuLyDauDuoi(String soDanh, String mien, LocalDate ngay, String tienDanh) {
        DoiChieuKetQuaDto dto = new DoiChieuKetQuaDto();
        dto.setSoDanh(soDanh);
        dto.setMien(mien);
        dto.setNgay(ngay);
        dto.setThu(chuyenNgaySangThu(ngay));
        dto.setCachDanh("DAUDUOI");
        dto.setTienDanh(tienDanh);

        // Tách số đầu - đuôi (dạng A-B)
        if (soDanh == null || !soDanh.matches("\\d{2}-\\d{2}")) {
            dto.setTrung(false);
            dto.setTienTrung(0);
            dto.setSaiLyDo(List.of("Số đánh không đúng định dạng A-B (vd: 62-15)"));
            return dto;
        }

        String[] parts = soDanh.split("-");
        String soDau = parts[0];
        String soDuoi = parts[1];

        DoiChieuKetQuaDto ketQuaDau = dauService.xuLyDau(soDau, mien, ngay, tienDanh);
        DoiChieuKetQuaDto ketQuaDuoi = duoiService.xuLyDuoi(soDuoi, mien, ngay, tienDanh);

        dto.setDanhSachDai(ketQuaDau.getDanhSachDai()); // dùng từ ĐẦU vì ĐUÔI có thể null

        boolean trungDau = ketQuaDau.isTrung();
        boolean trungDuoi = ketQuaDuoi.isTrung();

        if (trungDau && trungDuoi) {
            dto.setTrung(true);
            dto.setTenDai(ketQuaDau.getTenDai()); // hoặc chọn đài nào cũng được vì đều trúng
            dto.setGiaiTrung("Trúng cả ĐẦU và ĐUÔI");
            dto.setCachTrung("Trúng ĐẦU: " + ketQuaDau.getCachTrung() + ", ĐUÔI: " + ketQuaDuoi.getCachTrung());
            double tienTrung = Double.parseDouble(tienDanh) * (1000 + 70);
            dto.setTienTrung(tienTrung);
        } else {
            dto.setTrung(false);
            dto.setTienTrung(0);
            if (trungDau) {
                dto.setSaiLyDo(List.of("Trúng đầu nhưng không trúng đuôi"));
            } else if (trungDuoi) {
                dto.setSaiLyDo(List.of("Trúng đuôi nhưng không trúng đầu"));
            } else {
                dto.setSaiLyDo(List.of("Không trúng đầu hoặc đuôi"));
            }
        }

        return dto;
    }

    private String chuyenNgaySangThu(LocalDate ngay) {
        return switch (ngay.getDayOfWeek()) {
            case MONDAY -> "Thứ Hai";
            case TUESDAY -> "Thứ Ba";
            case WEDNESDAY -> "Thứ Tư";
            case THURSDAY -> "Thứ Năm";
            case FRIDAY -> "Thứ Sáu";
            case SATURDAY -> "Thứ Bảy";
            case SUNDAY -> "Chủ Nhật";
        };
    }

    public boolean laCachDanh(String cachDanhChuanHoa) {
        return cachDanhChuanHoa != null && cachDanhChuanHoa.contains("DAUDUOI");
    }
}
