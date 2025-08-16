
////package com.example.doxoso.service;
////
////import com.example.doxoso.model.DoiChieuKetQuaDto;
////import org.springframework.beans.factory.annotation.Autowired;
////import org.springframework.stereotype.Service;
////
////
////import java.time.LocalDate;
////import java.util.List;
////
////@Service
////public class DauDuoiService {
////
////    @Autowired
////    private DauService dauService;
////
////    @Autowired
////    private DuoiService duoiService;
////
////    @Autowired
////    private TinhTienService tinhTienService;
////
////    public DoiChieuKetQuaDto xuLyDauDuoi(String soDanh, String mien, LocalDate ngay, String tienDanh, String tenDai) {
////        DoiChieuKetQuaDto dto = new DoiChieuKetQuaDto();
////        dto.setSoDanh(soDanh);
////        dto.setMien(mien);
////        dto.setNgay(ngay);
////        dto.setThu(chuyenNgaySangThu(ngay));
////        dto.setCachDanh("DAUDUOI");
////        dto.setTienDanh(tienDanh);
////
////        if (soDanh == null || (!soDanh.matches("\\d{2}-\\d{2}") && !soDanh.matches("\\d{2}"))) {
////            dto.setTrung(false);
////            dto.setTienTrung(0.0);
////
////            dto.setSaiLyDo(List.of("Số đánh không đúng định dạng A-B hoặc A (vd: 62-15 hoặc 62)"));
////            return dto;
////        }
////
////        String[] parts = soDanh.contains("-") ? soDanh.split("-") : new String[]{soDanh, soDanh};
////        String soDau = parts[0];
////        String soDuoi = parts[1];
////
////        // Dò phần ĐẦU
////        DoiChieuKetQuaDto ketQuaDau = dauService.xuLyDau(soDau, mien, ngay, tienDanh, tenDai);
////        if (!ketQuaDau.isTrung() && ketQuaDau.getTienTrung() == 0 && ketQuaDau.getSaiLyDo() != null) {
////            return ketQuaDau; // nếu sai về đài, định dạng, ... thì trả về luôn lỗi đó
////        }
////
////        // Dò phần ĐUÔI
////        DoiChieuKetQuaDto ketQuaDuoi = duoiService.xuLyDuoi(soDuoi, mien, ngay, tienDanh, tenDai);
////        if (!ketQuaDuoi.isTrung() && ketQuaDuoi.getTienTrung() == 0 && ketQuaDuoi.getSaiLyDo() != null) {
////            return ketQuaDuoi;
////        }
////
////        // Tổng hợp kết quả
////        dto.setDanhSachDai(ketQuaDau.getDanhSachDai()); // Ưu tiên danh sách từ đầu
////        dto.setTenDai(ketQuaDau.getTenDai());
////
////        boolean trungDau = ketQuaDau.isTrung();
////        boolean trungDuoi = ketQuaDuoi.isTrung();
////        String mienTrungDau = ketQuaDau.getMien();
////        String mienTrungDuoi = ketQuaDuoi.getMien();
////
////        double tienDouble;
////        try {
////            tienDouble = Double.parseDouble(tienDanh);
////        } catch (Exception e) {
////            dto.setTrung(false);
////            dto.setTienTrung(0.0);
////
////            dto.setSaiLyDo(List.of("Tiền đánh không hợp lệ"));
////            return dto;
////        }
////
////        double tienTrung = tinhTienService.tinhTienDauDuoi(
////                trungDau, trungDuoi,
////                mienTrungDau, mienTrungDuoi,
////                tienDouble
////        );
////        dto.setTienTrung(tienTrung);
////
////        if (trungDau && trungDuoi) {
////            dto.setTrung(true);
////            dto.setGiaiTrung("Trúng cả ĐẦU và ĐUÔI");
////            dto.setCachTrung("ĐẦU: " + ketQuaDau.getCachTrung() + ", ĐUÔI: " + ketQuaDuoi.getCachTrung());
////        } else if (trungDau) {
////            dto.setTrung(true);
////            dto.setGiaiTrung("Chỉ trúng ĐẦU");
////            dto.setCachTrung("ĐẦU: " + ketQuaDau.getCachTrung());
////            dto.setSaiLyDo(List.of("Chỉ trúng đầu, không trúng đuôi"));
////        } else if (trungDuoi) {
////            dto.setTrung(true);
////            dto.setGiaiTrung("Chỉ trúng ĐUÔI");
////            dto.setCachTrung("ĐUÔI: " + ketQuaDuoi.getCachTrung());
////            dto.setSaiLyDo(List.of("Chỉ trúng đuôi, không trúng đầu"));
////        } else {
////            dto.setTrung(false);
////            dto.setTienTrung(0.0);
////
////            dto.setSaiLyDo(List.of("Không trúng đầu hoặc đuôi"));
////        }
////
////        return dto;
////    }
////
////
////    private String chuyenNgaySangThu(LocalDate ngay) {
////        return switch (ngay.getDayOfWeek()) {
////            case MONDAY -> "Thứ Hai";
////            case TUESDAY -> "Thứ Ba";
////            case WEDNESDAY -> "Thứ Tư";
////            case THURSDAY -> "Thứ Năm";
////            case FRIDAY -> "Thứ Sáu";
////            case SATURDAY -> "Thứ Bảy";
////            case SUNDAY -> "Chủ Nhật";
////        };
////    }
////
////
////}
////
//package com.example.doxoso.service;
//
//import com.example.doxoso.model.DoiChieuKetQuaDto;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//import java.time.LocalDate;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Objects;
//import java.util.stream.Collectors;
//
//@Service
//public class DauDuoiService {
//
//    @Autowired
//    private DauService dauService;
//
//    @Autowired
//    private DuoiService duoiService;
//
//    @Autowired
//    private TinhTienService tinhTienService;
//
//    /**
//     * Xử lý dò số ĐẦU - ĐUÔI.
//     * @param soDanh dạng "62-15" hoặc "62"
//     * @param mien miền đánh
//     * @param ngay ngày đánh
//     * @param tienDanh tổng tiền đánh (chia đều cho đầu và đuôi)
//     * @param tenDai tên đài (có thể null hoặc "2 ĐÀI")
//     */
//    public DoiChieuKetQuaDto xuLyDauDuoi(String soDanh, String mien, LocalDate ngay, String tienDanh, String tenDai) {
//        DoiChieuKetQuaDto dto = new DoiChieuKetQuaDto();
//        dto.setSoDanh(soDanh);
//        dto.setMien(mien);
//        dto.setNgay(ngay);
//        dto.setThu(chuyenNgaySangThu(ngay));
//        dto.setCachDanh("ĐẦU ĐUÔI");
//        dto.setTienDanh(tienDanh);
//
//        if (soDanh == null || (!soDanh.matches("\\d{2}-\\d{2}") && !soDanh.matches("\\d{2}"))) {
//            dto.setTrung(false);
//            dto.setTienTrung(0.0);
//            dto.setSaiLyDo(List.of("Số đánh không đúng định dạng A-B hoặc A (vd: 62-15 hoặc 62)"));
//            return dto;
//        }
//
//        String[] parts = soDanh.contains("-") ? soDanh.split("-") : new String[]{soDanh, soDanh};
//        String soDau = parts[0];
//        String soDuoi = parts[1];
//
//        double tienDanhDouble;
//        try {
//            tienDanhDouble = Double.parseDouble(tienDanh);
//        } catch (Exception e) {
//            dto.setTrung(false);
//            dto.setTienTrung(0.0);
//            dto.setSaiLyDo(List.of("Tiền đánh không hợp lệ"));
//            return dto;
//        }
//
//        double tienDau = tienDanhDouble / 2.0;
//        double tienDuoi = tienDanhDouble / 2.0;
//
//        DoiChieuKetQuaDto ketQuaDau = dauService.xuLyDau(soDau, mien, ngay, String.valueOf(tienDau), tenDai);
//        DoiChieuKetQuaDto ketQuaDuoi = duoiService.xuLyDuoi(soDuoi, mien, ngay, String.valueOf(tienDuoi), tenDai);
//
//        if ((!ketQuaDau.isTrung() && ketQuaDau.getTienTrung() == 0 && ketQuaDau.getSaiLyDo() != null) ||
//                (!ketQuaDuoi.isTrung() && ketQuaDuoi.getTienTrung() == 0 && ketQuaDuoi.getSaiLyDo() != null)) {
//            return !ketQuaDau.isTrung() ? ketQuaDau : ketQuaDuoi;
//        }
//
//        List<String> danhSachDai = new ArrayList<>();
//        if (ketQuaDau.getDanhSachDai() != null) danhSachDai.addAll(ketQuaDau.getDanhSachDai());
//        if (ketQuaDuoi.getDanhSachDai() != null) {
//            for (String dai : ketQuaDuoi.getDanhSachDai()) {
//                if (!danhSachDai.contains(dai)) danhSachDai.add(dai);
//            }
//        }
//        dto.setDanhSachDai(danhSachDai);
//
//        if (Objects.equals(ketQuaDau.getTenDai(), ketQuaDuoi.getTenDai())) {
//            dto.setTenDai(ketQuaDau.getTenDai());
//        } else {
//            dto.setTenDai(
//                    (ketQuaDau.getTenDai() != null ? ketQuaDau.getTenDai() : "") +
//                            ((!Objects.toString(ketQuaDau.getTenDai(), "").isEmpty() &&
//                                    !Objects.toString(ketQuaDuoi.getTenDai(), "").isEmpty()) ? ", " : "") +
//                            (ketQuaDuoi.getTenDai() != null ? ketQuaDuoi.getTenDai() : "")
//            );
//        }
//
//        boolean trungDau = ketQuaDau.isTrung();
//        boolean trungDuoi = ketQuaDuoi.isTrung();
//
//        double tienTrung = tinhTienService.tinhTienDauDuoi(
//                trungDau, trungDuoi,
//                ketQuaDau.getMien(), ketQuaDuoi.getMien(),
//                tienDanhDouble
//        );
//        dto.setTienTrung(tienTrung);
//
//        // ===== CÁCH TRÚNG =====
//        String soTrungDau = ketQuaDau.getKetQuaTungDai() != null
//                ? ketQuaDau.getKetQuaTungDai().stream()
//                .map(k -> k.getSoTrung() != null ? k.getTenDai() + ": " + k.getSoTrung() : null)
//                .filter(Objects::nonNull)
//                .collect(Collectors.joining(" | "))
//                : "";
//
//        String soTrungDuoi = ketQuaDuoi.getKetQuaTungDai() != null
//                ? ketQuaDuoi.getKetQuaTungDai().stream()
//                .map(k -> k.getSoTrung() != null ? k.getTenDai() + ": " + k.getSoTrung() : null)
//                .filter(Objects::nonNull)
//                .collect(Collectors.joining(" | "))
//                : "";
//
//        if (trungDau && trungDuoi) {
//            dto.setTrung(true);
//            dto.setGiaiTrung("Trúng cả ĐẦU và ĐUÔI");
//            dto.setCachTrung("ĐẦU: " + soTrungDau + ", ĐUÔI: " + soTrungDuoi);
//        } else if (trungDau) {
//            dto.setTrung(true);
//            dto.setGiaiTrung("Chỉ trúng ĐẦU");
//            dto.setCachTrung("ĐẦU: " + soTrungDau);
//            dto.setSaiLyDo(List.of("Chỉ trúng đầu, không trúng đuôi"));
//        } else if (trungDuoi) {
//            dto.setTrung(true);
//            dto.setGiaiTrung("Chỉ trúng ĐUÔI");
//            dto.setCachTrung("ĐUÔI: " + soTrungDuoi);
//            dto.setSaiLyDo(List.of("Chỉ trúng đuôi, không trúng đầu"));
//        } else {
//            dto.setTrung(false);
//            dto.setTienTrung(0.0);
//            dto.setSaiLyDo(List.of("Không trúng đầu hoặc đuôi"));
//        }
//
//        // ===== KẾT QUẢ CHI TIẾT =====
//        List<DoiChieuKetQuaDto.KetQuaTheoDai> chiTiet = new ArrayList<>();
//
//        for (String dai : danhSachDai) {
//            DoiChieuKetQuaDto.KetQuaTheoDai kq = new DoiChieuKetQuaDto.KetQuaTheoDai();
//            kq.setTenDai(dai);
//            kq.setMien(mien);
//
//            boolean trungDauDai = false;
//            boolean trungDuoiDai = false;
//            String soTrungText = "";
//
//            // Lấy thông tin từ KQ ĐẦU
//            ketQuaDau.getKetQuaTungDai().stream()
//                    .filter(k -> dai.equals(k.getTenDai()))
//                    .findFirst()
//                    .ifPresent(k -> {
//                        if (k.isTrung()) {
//                            kq.setSoTrung("ĐẦU: " + k.getSoTrung());
//                        }
//                    });
//
//            if (ketQuaDau.getKetQuaTungDai() != null) {
//                var optDau = ketQuaDau.getKetQuaTungDai().stream()
//                        .filter(k -> dai.equals(k.getTenDai()) && k.isTrung())
//                        .findFirst();
//                if (optDau.isPresent()) {
//                    trungDauDai = true;
//                    soTrungText = (soTrungText.isEmpty() ? "" : soTrungText + " | ") + "ĐẦU: " + optDau.get().getSoTrung();
//                }
//            }
//
//            // Lấy thông tin từ KQ ĐUÔI
//            if (ketQuaDuoi.getKetQuaTungDai() != null) {
//                var optDuoi = ketQuaDuoi.getKetQuaTungDai().stream()
//                        .filter(k -> dai.equals(k.getTenDai()) && k.isTrung())
//                        .findFirst();
//                if (optDuoi.isPresent()) {
//                    trungDuoiDai = true;
//                    soTrungText = (soTrungText.isEmpty() ? "" : soTrungText + " | ") + "ĐUÔI: " + optDuoi.get().getSoTrung();
//                }
//            }
//
//            kq.setSoTrung(soTrungText.isEmpty() ? null : soTrungText);
//
//            // Tính tiền trúng của riêng đài này
//            double tienTrungDai = tinhTienService.tinhTienDauDuoi(
//                    trungDauDai,
//                    trungDuoiDai,
//                    mien, // miền này có thể dùng luôn vì mỗi đài thuộc đúng 1 miền
//                    mien,
//                    tienDanhDouble // tổng tiền đánh nhưng tính trên 1 đài => cần chia cho số lượng đài
//                            / danhSachDai.size()
//            );
//            kq.setTienTrung(tienTrungDai);
//
//            kq.setTrung(tienTrungDai > 0);
//            chiTiet.add(kq);
//        }
//
//        dto.setKetQuaTungDai(chiTiet);
//
//
//        return dto;
//    }
//
//    private String chuyenNgaySangThu(LocalDate ngay) {
//        return switch (ngay.getDayOfWeek()) {
//            case MONDAY -> "Thứ Hai";
//            case TUESDAY -> "Thứ Ba";
//            case WEDNESDAY -> "Thứ Tư";
//            case THURSDAY -> "Thứ Năm";
//            case FRIDAY -> "Thứ Sáu";
//            case SATURDAY -> "Thứ Bảy";
//            case SUNDAY -> "Chủ Nhật";
//        };
//    }
//
//}
//
package com.example.doxoso.service;

import com.example.doxoso.model.DoiChieuKetQuaDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class DauDuoiService {

    @Autowired
    private DauService dauService;

    @Autowired
    private DuoiService duoiService;

    @Autowired
    private TinhTienService tinhTienService;

    public DoiChieuKetQuaDto xuLyDauDuoi(String soDanh, String mien, LocalDate ngay, String tienDanh, String tenDai) {
        DoiChieuKetQuaDto dto = taoDtoCoBan(soDanh, mien, ngay, tienDanh);

        // Chuẩn hóa và kiểm tra định dạng số đánh
        soDanh = chuanHoaSoDanh(soDanh);
        if (!hopLeSoDanh(soDanh)) {
            dto.setTrung(false);
            dto.setTienTrung(0.0);
            dto.setSaiLyDo(List.of("Số đánh không đúng định dạng A-B hoặc A (vd: 62-15 hoặc 62)"));
            return dto;
        }

        String[] parts = soDanh.contains("-") ? soDanh.split("-") : new String[]{soDanh, soDanh};
        String soDau = themSo0(parts[0]);
        String soDuoi = themSo0(parts[1]);

        double tienDanhDouble = parseTienDanh(tienDanh, dto);
        if (tienDanhDouble <= 0) return dto;

        // Chia tiền đánh theo số phần (đầu & đuôi)
        double tienDau = tienDanhDouble / 2.0;
        double tienDuoi = tienDanhDouble / 2.0;

        // Gọi xử lý ĐẦU và ĐUÔI
        DoiChieuKetQuaDto kqDau = dauService.xuLyDau(soDau, mien, ngay, String.valueOf(tienDau), tenDai);
        DoiChieuKetQuaDto kqDuoi = duoiService.xuLyDuoi(soDuoi, mien, ngay, String.valueOf(tienDuoi), tenDai);

        // Nếu cả 2 đều lỗi → trả về lỗi tổng hợp
        if (coLoi(kqDau) && coLoi(kqDuoi)) {
            dto.setSaiLyDo(Stream.concat(kqDau.getSaiLyDo().stream(), kqDuoi.getSaiLyDo().stream())
                    .distinct().toList());
            dto.setTrung(false);
            dto.setTienTrung(0.0);
            return dto;
        }
        // Nếu 1 bên lỗi → trả về lỗi đó
        if (coLoi(kqDau)) return kqDau;
        if (coLoi(kqDuoi)) return kqDuoi;

        // Gom danh sách đài
        List<String> danhSachDai = Stream.concat(
                Optional.ofNullable(kqDau.getDanhSachDai()).orElse(List.of()).stream(),
                Optional.ofNullable(kqDuoi.getDanhSachDai()).orElse(List.of()).stream()
        ).distinct().toList();
        dto.setDanhSachDai(danhSachDai);

        // Ghép tên đài
        dto.setTenDai(Stream.of(kqDau.getTenDai(), kqDuoi.getTenDai())
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.joining(", ")));

        boolean trungDau = kqDau.isTrung();
        boolean trungDuoi = kqDuoi.isTrung();

        // Tính tổng tiền trúng
        double tienTrungTong = tinhTienService.tinhTienDauDuoi(
                trungDau, trungDuoi,
                kqDau.getMien(), kqDuoi.getMien(),
                tienDanhDouble
        );
        dto.setTienTrung(tienTrungTong);

        // Cách trúng
        String soTrungDau = laySoTrung(kqDau.getKetQuaTungDai());
        String soTrungDuoi = laySoTrung(kqDuoi.getKetQuaTungDai());

        if (trungDau && trungDuoi) {
            dto.setTrung(true);
            dto.setGiaiTrung("Trúng cả ĐẦU và ĐUÔI");
            dto.setCachTrung("ĐẦU: " + soTrungDau + ", ĐUÔI: " + soTrungDuoi);
        } else if (trungDau) {
            dto.setTrung(true);
            dto.setGiaiTrung("Chỉ trúng ĐẦU");
            dto.setCachTrung("ĐẦU: " + soTrungDau);
            dto.setSaiLyDo(List.of("Chỉ trúng đầu, không trúng đuôi"));
        } else if (trungDuoi) {
            dto.setTrung(true);
            dto.setGiaiTrung("Chỉ trúng ĐUÔI");
            dto.setCachTrung("ĐUÔI: " + soTrungDuoi);
            dto.setSaiLyDo(List.of("Chỉ trúng đuôi, không trúng đầu"));
        } else {
            dto.setTrung(false);
            dto.setTienTrung(0.0);
            dto.setSaiLyDo(List.of("Không trúng đầu hoặc đuôi"));
        }

        // Kết quả chi tiết từng đài
        dto.setKetQuaTungDai(taoChiTietKetQua(danhSachDai, mien, tienDanhDouble, kqDau, kqDuoi));

        return dto;
    }

    // ==== HÀM PHỤ ====

    private DoiChieuKetQuaDto taoDtoCoBan(String soDanh, String mien, LocalDate ngay, String tienDanh) {
        DoiChieuKetQuaDto dto = new DoiChieuKetQuaDto();
        dto.setSoDanh(soDanh);
        dto.setMien(mien);
        dto.setNgay(ngay);
        dto.setThu(chuyenNgaySangThu(ngay));
        dto.setCachDanh("ĐẦU ĐUÔI");
        dto.setTienDanh(tienDanh);
        return dto;
    }

    private String chuanHoaSoDanh(String so) {
        return so == null ? null : so.trim().replaceAll("\\s+", "");
    }

    private boolean hopLeSoDanh(String so) {
        return so != null && so.matches("\\d{1,2}(-\\d{1,2})?");
    }

    private String themSo0(String so) {
        return so.length() == 1 ? "0" + so : so;
    }

    private double parseTienDanh(String tienDanh, DoiChieuKetQuaDto dto) {
        try {
            return Double.parseDouble(tienDanh);
        } catch (Exception e) {
            dto.setTrung(false);
            dto.setTienTrung(0.0);
            dto.setSaiLyDo(List.of("Tiền đánh không hợp lệ"));
            return -1;
        }
    }

    private boolean coLoi(DoiChieuKetQuaDto dto) {
        return !dto.isTrung() && dto.getTienTrung() == 0 && dto.getSaiLyDo() != null;
    }

    private String laySoTrung(List<DoiChieuKetQuaDto.KetQuaTheoDai> kqList) {
        return kqList == null ? "" :
                kqList.stream()
                        .map(k -> k.getSoTrung() != null ? k.getTenDai() + ": " + k.getSoTrung() : null)
                        .filter(Objects::nonNull)
                        .collect(Collectors.joining(" | "));
    }

    private List<DoiChieuKetQuaDto.KetQuaTheoDai> taoChiTietKetQua(
            List<String> danhSachDai, String mien, double tienDanhTong,
            DoiChieuKetQuaDto kqDau, DoiChieuKetQuaDto kqDuoi) {

        List<DoiChieuKetQuaDto.KetQuaTheoDai> chiTiet = new ArrayList<>();
        double tienMoiDai = tienDanhTong / danhSachDai.size();

        for (String dai : danhSachDai) {
            boolean trungDau = kqDau.getKetQuaTungDai() != null &&
                    kqDau.getKetQuaTungDai().stream().anyMatch(k -> dai.equals(k.getTenDai()) && k.isTrung());

            boolean trungDuoi = kqDuoi.getKetQuaTungDai() != null &&
                    kqDuoi.getKetQuaTungDai().stream().anyMatch(k -> dai.equals(k.getTenDai()) && k.isTrung());

            String soTrungText = Stream.of(
                    trungDau ? "ĐẦU: " + laySoTheoDai(kqDau, dai) : null,
                    trungDuoi ? "ĐUÔI: " + laySoTheoDai(kqDuoi, dai) : null
            ).filter(Objects::nonNull).collect(Collectors.joining(" | "));

            double tienTrung = tinhTienService.tinhTienDauDuoi(
                    trungDau, trungDuoi, mien, mien, tienMoiDai
            );

            DoiChieuKetQuaDto.KetQuaTheoDai kq = new DoiChieuKetQuaDto.KetQuaTheoDai();
            kq.setTenDai(dai);
            kq.setMien(mien);
            kq.setSoTrung(soTrungText.isEmpty() ? null : soTrungText);
            kq.setTienTrung(tienTrung);
            kq.setTrung(tienTrung > 0);

            chiTiet.add(kq);
        }
        return chiTiet;
    }

    private String laySoTheoDai(DoiChieuKetQuaDto dto, String dai) {
        return dto.getKetQuaTungDai().stream()
                .filter(k -> dai.equals(k.getTenDai()) && k.isTrung())
                .map(DoiChieuKetQuaDto.KetQuaTheoDai::getSoTrung)
                .findFirst().orElse(null);
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
}
