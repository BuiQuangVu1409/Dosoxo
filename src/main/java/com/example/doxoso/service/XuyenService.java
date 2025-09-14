package com.example.doxoso.service;

import com.example.doxoso.model.DoiChieuKetQuaDto;
import com.example.doxoso.model.SoNguoiChoi;
import com.example.doxoso.repository.KetQuaMienBacRepository;
import com.example.doxoso.repository.KetQuaMienNamRepository;
import com.example.doxoso.repository.KetQuaMienTrungRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.PrivateKey;
import java.text.Normalizer;
import java.time.LocalDate;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
////
////@Service
////public class XuyenService {
////
////    @Autowired
////    private SoCuoiService soCuoiService;
////
////    // Nhận diện có phải cách đánh XUYÊN không
////    public boolean laCachDanhXuyen(String cachDanh) {
////        if (cachDanh == null) return false;
////        return cachDanh.toUpperCase().replace(" ", "").startsWith("XUYEN");
////    }
////
////    // Tách số: "02-34-12" → ["02", "34", "12"]
////    public List<String> tachSoDaDanh(String soDanh) {
////        return Arrays.stream(soDanh.split("[-,]"))
////                .map(s -> {
////                    String so = s.trim().replaceFirst("^0+", "");
////                    return so.length() == 1 ? "0" + so : so;
////                })
////                .collect(Collectors.toList());
////    }
////
////    // Kiểm tra số trúng xuyên
////    public Optional<String> xuLyTrungXuyen(String cachDanh, String soDanh, LocalDate ngay, String mien) {
////        if (!laCachDanhXuyen(cachDanh)) return Optional.empty();
////
////        List<String> soNguoiDanh = tachSoDaDanh(soDanh);
////        Map<String, List<String>> mapSoTrung = soCuoiService.lay2SoCuoiCuaTatCaDai(mien, ngay);
////
////        for (Map.Entry<String, List<String>> entry : mapSoTrung.entrySet()) {
////            String tenDai = entry.getKey();
////            List<String> soTrungTrongDai = entry.getValue();
////
////            if (laTrungXuyen(soNguoiDanh, soTrungTrongDai)) {
////                return Optional.of(tenDai); // ✅ Trúng xuyên tại đài này
////            }
////        }
////
////        return Optional.empty(); // ❌ Không trúng đài nào
////    }
////
////    // Tất cả số người đánh đều nằm trong list số trúng của đài
////    public boolean laTrungXuyen(List<String> soNguoiDanh, List<String> soTrungTrongDai) {
////        return soNguoiDanh.stream().allMatch(soTrungTrongDai::contains);
////    }
////}
@Service
public class XuyenService {

    @Autowired
    private KetQuaMienBacRepository bacRepo;

    @Autowired
    private KetQuaMienTrungRepository trungRepo;

    @Autowired
    private KetQuaMienNamRepository namRepo;

    @Autowired
    private TinhTienService tinhTienService;
    /**
     * Kiểm tra có phải cách đánh Xuyên hay không
     */
    public boolean laCachDanhXuyen(String cachDanh) {
        String cd = chuanHoaCachDanhXuyen(cachDanh);
        return cd != null && cd.startsWith("XUYEN");
    }

    /**
     * Xử lý dò số Xuyên trên nhiều đài trong 1 miền
     */
    public DoiChieuKetQuaDto xuLyXuyen(SoNguoiChoi so) {
        DoiChieuKetQuaDto dto = new DoiChieuKetQuaDto();
        dto.setSoDanh(so.getSoDanh());
        dto.setCachTrung(so.getCachDanh());

        LocalDate ngay = so.getNgay();
        String mien = so.getMien().toUpperCase();

        // 1️⃣ Lấy danh sách đài trong miền
        List<String> danhSachDai = layDanhSachDaiTheoNgayVaMien(ngay, mien);

        // 2️⃣ Parse tiền đánh
        double tienDanhDouble;

        try {
            tienDanhDouble = Double.parseDouble(so.getTienDanh());
        } catch (NumberFormatException e) {
            dto.setTrung(false);
            dto.setTienTrung(0.0);
            dto.setSaiLyDo(List.of("Tiền đánh không hợp lệ: " + so.getTienDanh()));
            dto.setDanhSachDai(danhSachDai);
            dto.setKetQuaTungDai(List.of());
            return dto;
        }

        // 3️⃣ Lấy kết quả xổ số từ repo theo miền
        List<Object> ketQuaTongHop = new ArrayList<>();
        if (mien.contains("BẮC")) ketQuaTongHop.addAll(bacRepo.findAllByNgay(ngay));
        if (mien.contains("TRUNG")) ketQuaTongHop.addAll(trungRepo.findAllByNgay(ngay));
        if (mien.contains("NAM")) ketQuaTongHop.addAll(namRepo.findAllByNgay(ngay));

        Map<String, List<Object>> ketQuaTheoDai = ketQuaTongHop.stream()
                .filter(kq -> danhSachDai.contains(((String)getField(kq, "getTenDai")).toUpperCase()))
                .collect(Collectors.groupingBy(kq -> ((String)getField(kq, "getTenDai")).toUpperCase()));

        List<DoiChieuKetQuaDto.KetQuaTheoDai> ketQuaTungDai = new ArrayList<>();
        boolean coTrung = false;

        // 4️⃣ Dò từng đài
        for (String tenDai : danhSachDai) {
            DoiChieuKetQuaDto.KetQuaTheoDai kqDai = new DoiChieuKetQuaDto.KetQuaTheoDai();
            kqDai.setTenDai(tenDai);
            kqDai.setMien(mien);

            List<Object> ketQuaDai = ketQuaTheoDai.getOrDefault(tenDai, List.of());

            List<String> soChuaTrung = new ArrayList<>(Arrays.asList(so.getSoDanh().split("-")));

            List<String> soTrung = ketQuaDai.stream()
                    .map(kq -> (String) getField(kq, "getSoTrung"))
                    .filter(Objects::nonNull)
                    .map(s -> s.length() >= 2 ? s.substring(s.length() - 2) : "")
                    .collect(Collectors.toList());

            boolean trungDai = soChuaTrung.stream().allMatch(soTrung::contains);

            if (trungDai) {
                kqDai.setTrung(true);
                kqDai.setSoTrung(String.join(",", soChuaTrung));
//                kqDai.setGiaiTrung(new ArrayList<>(soChuaTrung));
                kqDai.setSoLanTrung(1);

                // ✅ Tính tiền đúng kiểu double
                double tien = tinhTienService.tinhTienXuyen(

                        so.getCachDanh(),  // cachDanh
                        so.getTienDanh(),  // tienDanh dưới dạng String
                        mien               // mien
                );

                kqDai.setTienTrung(tien);

                coTrung = true;
            } else {
                kqDai.setTrung(false);
                List<String> thieu = soChuaTrung.stream().filter(s -> !soTrung.contains(s)).toList();
                kqDai.setLyDo("Thiếu số: " + String.join(",", thieu));
                kqDai.setSoLanTrung(0);
                kqDai.setTienTrung(0.0);
            }

            ketQuaTungDai.add(kqDai);
        }

        dto.setTrung(coTrung);
        dto.setKetQuaTungDai(ketQuaTungDai);
        dto.setDanhSachDai(danhSachDai);

        return dto;
    }
// hiện tại dang dò và lấy kết quả miền và đài dựa vào database có thể thay đổi dò miền và đài dựa vào lịch theo ngày
    private List<String> layDanhSachDaiTheoNgayVaMien(LocalDate ngay, String mien) {
        switch (mien.toUpperCase()) {
            case "BẮC":
            case "MIỀN BẮC":
                return bacRepo.findAllByNgay(ngay).stream()
                        .map(kq -> kq.getTenDai().trim().toUpperCase())
                        .distinct()
                        .toList();
            case "TRUNG":
            case "MIỀN TRUNG":
                return trungRepo.findAllByNgay(ngay).stream()
                        .map(kq -> kq.getTenDai().trim().toUpperCase())
                        .distinct()
                        .toList();
            case "NAM":
            case "MIỀN NAM":
                return namRepo.findAllByNgay(ngay).stream()
                        .map(kq -> kq.getTenDai().trim().toUpperCase())
                        .distinct()
                        .toList();
            default:
                return Collections.emptyList();
        }
    }


    @SuppressWarnings("unchecked")
    private <T> T getField(Object obj, String methodName) {
        try {
            return (T) obj.getClass().getMethod(methodName).invoke(obj);
        } catch (Exception e) {
            throw new RuntimeException("Lỗi phản xạ khi lấy field: " + methodName, e);
        }
    }

    // Chuẩn hóa cách đánh XUYÊN
    public String chuanHoaCachDanhXuyen(String cachDanh) {
        if (cachDanh == null) return null;
        String cd = removeDiacritics(cachDanh).toUpperCase().replaceAll("[\\s\\.,]+", "");
        cd = cd.replace("XIEN", "XUYEN");

        if (cd.matches("\\d+XUYEN\\d*")) {
            String so = cd.replaceAll("\\D+", "");
            return "XUYEN" + so;
        }
        if (cd.matches("XUYEN\\d+")) return cd;
        if (cd.equals("XUYEN") || cd.equals("XIEN")) return "XUYEN2";
        return cd;
    }

    private String removeDiacritics(String input) {
        String normalized = java.text.Normalizer.normalize(input, java.text.Normalizer.Form.NFD);
        return java.util.regex.Pattern.compile("\\p{InCombiningDiacriticalMarks}+")
                .matcher(normalized).replaceAll("");
    }
}

//package com.example.doxoso.service;
//
//import com.example.doxoso.model.DoiChieuKetQuaDto;
//import com.example.doxoso.repository.KetQuaMienBacRepository;
//import com.example.doxoso.repository.KetQuaMienNamRepository;
//import com.example.doxoso.repository.KetQuaMienTrungRepository;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//import java.text.Normalizer;
//import java.time.DayOfWeek;
//import java.time.LocalDate;
//import java.util.*;
//import java.util.concurrent.atomic.AtomicBoolean;
//import java.util.regex.Pattern;
//import java.util.stream.Collectors;
//
//@Service
//public class XuyenService {
//
//    @Autowired
//    private KetQuaMienBacRepository bacRepo;
//
//    @Autowired
//    private KetQuaMienTrungRepository trungRepo;
//
//    @Autowired
//    private KetQuaMienNamRepository namRepo;
//
//    @Autowired
//    private TinhTienService tinhTienService;
//
//
//
//    public DoiChieuKetQuaDto xuLyXuyen(String soDanh, String mien, LocalDate ngay, String tienDanh, String tenDai) {
//        DoiChieuKetQuaDto dto = new DoiChieuKetQuaDto();
//        dto.setSoDanh(soDanh);
//        dto.setTenDai(tenDai != null ? tenDai.trim() : "");
//        dto.setMien(mien);
//        dto.setNgay(ngay);
//        dto.setThu(chuyenNgaySangThu(ngay));
//        dto.setCachDanh("XUYEN");
//        dto.setCachTrung("XUYEN");
//        dto.setTienDanh(tienDanh);
//
//        // Luôn khởi tạo danh sách kết quả để tránh NullPointerException
//        List<DoiChieuKetQuaDto.KetQuaTheoDai> ketQuaTungDaiList = new ArrayList<>();
//        dto.setKetQuaTungDai(ketQuaTungDaiList);
//
//        // Validate cơ bản
//        if (soDanh == null || tienDanh == null || tenDai == null || tenDai.isBlank()) {
//            dto.setTrung(false);
//            dto.setTienTrung(0.0);
//            dto.setSaiLyDo(List.of("Thiếu thông tin bắt buộc"));
//            return dto;
//        }
//
//        // Tách số người chơi nhập (Xuyên 2, 3…)
//        List<String> cacSo = Arrays.stream(soDanh.split("-"))
//                .map(String::trim)
//                .filter(s -> !s.isEmpty())
//                .collect(Collectors.toList());
//
//        if (cacSo.isEmpty()) {
//            dto.setTrung(false);
//            dto.setTienTrung(0.0);
//            dto.setSaiLyDo(List.of("Số đánh không hợp lệ"));
//            return dto;
//        }
//
//        // Lấy danh sách đài thực tế trong ngày
//        List<String> dsDaiTrongNgay = layDanhSachDaiTrongNgay(mien, ngay);
//        if (dsDaiTrongNgay.isEmpty()) {
//            dto.setTrung(false);
//            dto.setTienTrung(0.0);
//            dto.setSaiLyDo(List.of("Không có đài mở thưởng trong ngày " + ngay));
//            return dto;
//        }
//
//        // Xác định đài người chơi chọn
//        List<String> dsDaiNguoiChoi = new ArrayList<>();
//        String tenDaiNormalized = tenDai.trim().toUpperCase();
//        if (tenDaiNormalized.matches("\\d+\\s*ĐÀI")) {
//            int soDaiNguoiNhap = Integer.parseInt(tenDaiNormalized.split("\\s+")[0]);
//            if (soDaiNguoiNhap == dsDaiTrongNgay.size()) {
//                dsDaiNguoiChoi.addAll(dsDaiTrongNgay);
//            } else {
//                dto.setTrung(false);
//                dto.setTienTrung(0.0);
//                dto.setSaiLyDo(List.of("Số đài nhập (" + soDaiNguoiNhap + ") không khớp với số đài mở thưởng trong ngày (" + dsDaiTrongNgay.size() + ")"));
//                dto.setDanhSachDai(dsDaiTrongNgay);
//                return dto;
//            }
//        } else {
//            dsDaiNguoiChoi = Arrays.stream(tenDai.split(","))
//                    .map(String::trim)
//                    .filter(s -> !s.isEmpty())
//                    .collect(Collectors.toList());
//
//            List<String> dsSai = dsDaiNguoiChoi.stream()
//                    .filter(dai -> dsDaiTrongNgay.stream().noneMatch(d -> d.equalsIgnoreCase(dai)))
//                    .collect(Collectors.toList());
//
//            if (!dsSai.isEmpty()) {
//                dto.setTrung(false);
//                dto.setTienTrung(0.0);
//                dto.setSaiLyDo(List.of("Tên đài không hợp lệ: " + String.join(", ", dsSai)));
//                dto.setDanhSachDai(dsDaiTrongNgay);
//                return dto;
//            }
//        }
//
//        // Chuyển tiền đánh
//        double tienDanhDouble;
//        try {
//            tienDanhDouble = Double.parseDouble(tienDanh);
//        } catch (NumberFormatException e) {
//            dto.setTrung(false);
//            dto.setTienTrung(0.0);
//            dto.setSaiLyDo(List.of("Tiền đánh không hợp lệ"));
//            return dto;
//        }
//
//        double tienDanhMoiDai = tienDanhDouble / dsDaiNguoiChoi.size();
//        AtomicBoolean daTrung = new AtomicBoolean(false);
//        double[] tongTienTrung = {0.0};
//
//        // Xử lý từng đài
//        ketQuaTungDaiList.addAll(dsDaiNguoiChoi.stream().map(dai -> {
//            DoiChieuKetQuaDto.KetQuaTheoDai ketQuaDai = new DoiChieuKetQuaDto.KetQuaTheoDai();
//            ketQuaDai.setTenDai(dai);
//            ketQuaDai.setMien(mien);
//
//            List<?> ketQuaDaiHienTai = getKetQuaTheoDaiVaNgay(mien, dai, ngay);
//            List<String> giaiTrung = new ArrayList<>();
//            List<String> soTrung = new ArrayList<>();
//
//            for (Object kq : ketQuaDaiHienTai) {
//                List<String> tatCaGiai = getAllGiai(kq);
//                for (String giai : tatCaGiai) {
//                    for (String so : cacSo) {
//                        if (giai.endsWith(so)) {
//                            giaiTrung.add(giai);
//                            soTrung.add(so);
//                        }
//                    }
//                }
//            }
//
//            if (!giaiTrung.isEmpty()) {
//                ketQuaDai.setTrung(true);
//                ketQuaDai.setGiaiTrung(giaiTrung);
//                ketQuaDai.setSoTrung(String.join(",", soTrung));
//                ketQuaDai.setSoLanTrung(giaiTrung.size());
//
//                double tienTrungDai = tinhTienService.tinhTienTrung("XUYEN", mien, tienDanh) * ketQuaDai.getSoLanTrung();
//                ketQuaDai.setTienTrung(tienTrungDai);
//
//                tongTienTrung[0] += tienTrungDai;
//                daTrung.set(true);
//            } else {
//                ketQuaDai.setTrung(false);
//                ketQuaDai.setTienTrung(0.0);
//                ketQuaDai.setLyDo("Không có số trúng");
//                ketQuaDai.setSoLanTrung(0);
//            }
//
//            return ketQuaDai;
//        }).collect(Collectors.toList()));
//
//        // Set dữ liệu cuối cùng
//        dto.setTrung(daTrung.get());
//        dto.setTienTrung(tongTienTrung[0]);
//        dto.setDanhSachDai(dsDaiNguoiChoi);
//
//        // ✅ Luôn giữ ketQuaTungDai không null
//        dto.setKetQuaTungDai(ketQuaTungDaiList);
//
//        return dto;
//    }
//
//
//    /**
//     //     * Nhận biết cách đánh có phải xuyên không
//     //     */
//    public boolean laCachDanhXuyen(String cachDanh) {
//        String cd = chuanHoaCachDanhXuyen(cachDanh);
//        return cd != null && cd.startsWith("XUYEN");
//    }
//    private List<String> layDanhSachDaiTrongNgay(String mien, LocalDate ngay) {
//        switch (mien.toUpperCase()) {
//            case "MIỀN BẮC":
//                return bacRepo.findAllByNgay(ngay).stream()
//                        .map(kq -> kq.getTenDai().trim())
//                        .distinct()
//                        .toList();
//            case "MIỀN TRUNG":
//                return trungRepo.findAllByNgay(ngay).stream()
//                        .map(kq -> kq.getTenDai().trim())
//                        .distinct()
//                        .toList();
//            case "MIỀN NAM":
//                return namRepo.findAllByNgay(ngay).stream()
//                        .map(kq -> kq.getTenDai().trim())
//                        .distinct()
//                        .toList();
//            default:
//                return Collections.emptyList();
//        }
//    }
//
//
//    private List<?> getKetQuaTheoDaiVaNgay(String mien, String tenDai, LocalDate ngay) {
//        switch (mien.toUpperCase()) {
//            case "MIỀN BẮC":
//                return bacRepo.findAllByNgay(ngay).stream()
//                        .filter(kq -> chuanHoa(kq.getTenDai()).equals(chuanHoa(tenDai)))
//                        .toList();
//            case "MIỀN TRUNG":
//                return trungRepo.findAllByNgay(ngay).stream()
//                        .filter(kq -> chuanHoa(kq.getTenDai()).equals(chuanHoa(tenDai)))
//                        .toList();
//            case "MIỀN NAM":
//                return namRepo.findAllByNgay(ngay).stream()
//                        .filter(kq -> chuanHoa(kq.getTenDai()).equals(chuanHoa(tenDai)))
//                        .toList();
//            default:
//                return Collections.emptyList();
//        }
//    }
//
//    private List<String> getAllGiai(Object kqEntity) {
//        // Dùng reflection để lấy tất cả field giaiDacBiet, giaiNhat, giaiNhi1… về List<String>
//        List<String> tatCaGiai = new ArrayList<>();
//        try {
//            for (var method : kqEntity.getClass().getMethods()) {
//                if (method.getName().startsWith("getGiai") && method.getReturnType() == String.class) {
//                    String val = (String) method.invoke(kqEntity);
//                    if (val != null && !val.isBlank()) tatCaGiai.add(val.trim());
//                }
//            }
//        } catch (Exception e) {
//            throw new RuntimeException("Lỗi khi lấy tất cả giải từ entity", e);
//        }
//        return tatCaGiai;
//    }
//
//    private String chuyenNgaySangThu(LocalDate ngay) {
//        DayOfWeek day = ngay.getDayOfWeek();
//        return switch (day) {
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
//    private String chuanHoa(String input) {
//        if (input == null) return "";
//        return Normalizer.normalize(input, Normalizer.Form.NFD)
//                .replaceAll("đ", "d")
//                .replaceAll("Đ", "D")
//                .replaceAll("\\p{M}", "")
//                .toUpperCase()
//                .trim()
//                .replaceAll("\\s+", "");
//    }
//    public String chuanHoaCachDanhXuyen(String cachDanh) {
//        if (cachDanh == null) return null;
//
//        // 1. Chuẩn hóa chuỗi gốc
//        String cd = removeDiacritics(cachDanh)      // bỏ dấu tiếng Việt
//                .toUpperCase()                      // viết hoa
//                .replaceAll("[\\s\\.,]+", "");      // bỏ khoảng trắng, dấu chấm, phẩy
//
//        // 2. Thay thế "XIÊN" -> "XUYEN"
//        cd = cd.replace("XIEN", "XUYEN");
//
//        // 3. Trường hợp dạng "2XUYEN" hoặc "3XUYEN" => chuẩn hóa "XUYEN2", "XUYEN3"
//        if (cd.matches("\\d+XUYEN\\d*")) {
//            String so = cd.replaceAll("\\D+", ""); // lấy số
//            return "XUYEN" + so;
//        }
//
//        // 4. Trường hợp dạng "XUYEN2", "XUYEN3" => đã chuẩn
//        if (cd.matches("XUYEN\\d+")) {
//            return cd;
//        }
//
//        // 5. Trường hợp chỉ ghi "XUYEN" hoặc "XIEN" => mặc định là "XUYEN2"
//        if (cd.equals("XUYEN") || cd.equals("XIEN")) {
//            return "XUYEN2";
//        }
//
//        // 6. Fallback: trả về dạng đã chuẩn hóa
//        return cd;
//    }
//        private String removeDiacritics(String input) {
//        String normalized = Normalizer.normalize(input, Normalizer.Form.NFD);
//        return Pattern.compile("\\p{InCombiningDiacriticalMarks}+")
//                .matcher(normalized)
//                .replaceAll("");
//    }
//
//}

