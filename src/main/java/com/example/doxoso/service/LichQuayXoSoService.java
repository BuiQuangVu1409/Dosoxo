package com.example.doxoso.service;
import com.example.doxoso.model.LichQuayXoSo;
import org.springframework.stereotype.Service;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
    @Service
    public class LichQuayXoSoService {

        private static final Map<String, List<String>> LICH_MIEN_BAC = Map.of(
                "Thứ Hai", List.of("HÀ NỘI"),
                "Thứ Ba", List.of("HÀ NỘI"),
                "Thứ Tư", List.of("HÀ NỘI"),
                "Thứ Năm", List.of("HÀ NỘI"),
                "Thứ Sáu", List.of("HÀ NỘI"),
                "Thứ Bảy", List.of("HÀ NỘI"),
                "Chủ Nhật", List.of("HÀ NỘI")
        );

        private static final Map<String, List<String>> LICH_MIEN_TRUNG = Map.of(
                "Thứ Hai", List.of("PHÚ YÊN", "THỪA THIÊN HUẾ"),
                "Thứ Ba", List.of("ĐẮK LẮK", "QUẢNG NAM"),
                "Thứ Tư", List.of("ĐÀ NẴNG", "KHÁNH HÒA"),
                "Thứ Năm", List.of("BÌNH ĐỊNH", "QUẢNG BÌNH", "QUẢNG TRỊ"),
                "Thứ Sáu", List.of("GIA LAI", "NINH THUẬN"),
                "Thứ Bảy", List.of("ĐẮK NÔNG", "QUẢNG NGÃI"),
                "Chủ Nhật", List.of("KON TUM", "KHÁNH HÒA")
        );

        private static final Map<String, List<String>> LICH_MIEN_NAM = Map.of(
                "Thứ Hai", List.of("TP.HỒ CHÍ MINH", "ĐỒNG THÁP", "CÀ MAU"),
                "Thứ Ba", List.of("BẾN TRE", "VŨNG TÀU", "BẠC LIÊU"),
                "Thứ Tư", List.of("CẦN THƠ", "SÓC TRĂNG", "ĐỒNG NAI"),
                "Thứ Năm", List.of("AN GIANG", "TÂY NINH", "BÌNH THUẬN"),
                "Thứ Sáu", List.of("VĨNH LONG", "BÌNH DƯƠNG", "TRÀ VINH"),
                "Thứ Bảy", List.of("TP.HỒ CHÍ MINH", "LONG AN", "BÌNH PHƯỚC", "HẬU GIANG"),
                "Chủ Nhật", List.of("KIÊN GIANG", "TIỀN GIANG", "ĐÀ LẠT")
        );

        public LichQuayXoSo traCuuTheoNgay(LocalDate ngay) {
            String thu = chuyenDoiThu(ngay);
            Map<String, List<String>> ketQua = new HashMap<>();
            ketQua.put("MIỀN BẮC", LICH_MIEN_BAC.getOrDefault(thu, List.of()));
            ketQua.put("MIỀN TRUNG", LICH_MIEN_TRUNG.getOrDefault(thu, List.of()));
            ketQua.put("MIỀN NAM", LICH_MIEN_NAM.getOrDefault(thu, List.of()));

            return new LichQuayXoSo(ngay.toString(), thu, ketQua);
        }

        private String chuyenDoiThu(LocalDate date) {
            DayOfWeek day = date.getDayOfWeek();
            return switch (day) {
                case MONDAY -> "Thứ Hai";
                case TUESDAY -> "Thứ Ba";
                case WEDNESDAY -> "Thứ Tư";
                case THURSDAY -> "Thứ Năm";
                case FRIDAY -> "Thứ Sáu";
                case SATURDAY -> "Thứ Bảy";
                case SUNDAY -> "Chủ Nhật";
            };
        }

    }
