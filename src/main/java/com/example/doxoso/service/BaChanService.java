//
//package com.example.doxoso.service;
//
//import com.example.doxoso.model.DoiChieuKetQuaDto;
//import org.springframework.stereotype.Service;
//
//import java.lang.reflect.Method;
//import java.util.List;
//
//@Service
//public class BaChanService {
//
//    // Hàm chính để xử lý 3 chân
//    public void xuLyBaChan(DoiChieuKetQuaDto dto, String soDanh, String tienChuoi, String mien, List<Object> danhSachKetQua) {
//        double[] tien = tachTienDanhBaChan(tienChuoi);
//        double tienBaoLo = tien[0];
//        double tienThuong = tien[1];
//        double tienDacBiet = tien[2];
//
//        double maxTongTien = 0;
//        String loaiTrungMax = null;
//        String giaiTrungMax = null;
//
//        double tienBaoLoMax = 0;
//        double tienThuongMax = 0;
//        double tienDacBietMax = 0;
//
//        String mienChuan = mien.trim().toUpperCase();
//
//
//        for (Object kq : danhSachKetQua) {
//            String soTrung = getField(kq, "getSoTrung");
//            String giaiRaw = getField(kq, "getGiai");
//            String giai = chuanHoaTenGiai(giaiRaw);
//
//            if (soTrung == null || !chuaDu3ChuSo(soTrung, soDanh)) continue;
//
//            double tienBL = 0, tienT = 0, tienDB = 0;
//            String loaiTrung = "";
//            // tính tiền đặc biệt
//            if ("ĐẶC BIỆT".equalsIgnoreCase(giai)) {
//                if (mienChuan.contains("BẮC")) {
//                    tienBL = tienBaoLo * 600 / 23;
//                    tienT = tienThuong * 600 / 10;
//                    tienDB = tienDacBiet * 600;
//                } else if (mienChuan.contains("TRUNG")) {
//                    tienBL = tienBaoLo * 600 / 17;
//                    tienT = tienThuong * 100;
//                    tienDB = tienDacBiet * 600;
//                } else if (mienChuan.contains("NAM")) {
//                    tienBL = tienBaoLo * 600 / 17;
//                    tienT = tienThuong * 100;
//                    tienDB = tienDacBiet * 600;
//                }
//                loaiTrung = "Đặc biệt";
//                //tính tiền thượng
//            } else if (isLoThuong(mien, giai)) {
//                if (mienChuan.contains("BẮC")) {
//                    tienBL = tienBaoLo * 600 / 23;
//                    tienT = tienThuong * 600 / 10;
//                } else if (mienChuan.contains("TRUNG")) {
//                    tienBL = tienBaoLo * 600 / 17;
//                    tienT = tienThuong * 100;
//                } else if (mienChuan.contains("NAM")) {
//                    tienBL = tienBaoLo * 600 / 17;
//                    tienT = tienThuong * 100;
//                }
//                loaiTrung = "Lô thượng";
//                // tính tiền lô
//            } else {
//
//                if (mienChuan.contains("BẮC")) {
//                    tienBL = tienBaoLo * 600 / 23;
//                } else if (mienChuan.contains("TRUNG")) {
//                    tienBL = tienBaoLo * 600 / 17;
//                } else if (mienChuan.contains("NAM")) {
//                    tienBL = tienBaoLo * 600 / 17;
//                }
//                loaiTrung = "Bao lô";
//            }
//
//            double tongTien = tienBL + tienT + tienDB;
//
//            if (tongTien > maxTongTien) {
//                maxTongTien = tongTien;
//                loaiTrungMax = loaiTrung;
//                giaiTrungMax = giai;
//
//                tienBaoLoMax = tienBL;
//                tienThuongMax = tienT;
//                tienDacBietMax = tienDB;
//            }
//        }
//
//        if (maxTongTien > 0) {
//            dto.setTrung(true);
//
//            dto.setCachTrung(loaiTrungMax);
//            dto.setGiaiTrung(giaiTrungMax);
//// làm tròn 2 số thập phân
//            dto.setTienTrung((double) Math.round(maxTongTien));
//
//            dto.setTienTrungBaoLo((double) Math.round(tienBaoLoMax));
//            dto.setTienTrungThuong((double) Math.round(tienThuongMax));
//            dto.setTienTrungDacBiet((double) Math.round(tienDacBietMax));
//
//
//
//        } else {
//            dto.setTrung(false);
//            dto.setTienTrung(0.0);
//
//            dto.setSaiLyDo(List.of("Sai số"));
//        }
//    }
//
//    public double[] tachTienDanhBaChan(String tienDanh) {
//        double[] tien = new double[] {0.0, 0.0, 0.0}; // [baoLo, thuong, dacBiet]
//
//        if (tienDanh == null || tienDanh.trim().isEmpty()) return tien;
//
//        String[] parts = tienDanh.trim().split("-");
//        for (int i = 0; i < Math.min(3, parts.length); i++) {
//            String part = parts[i].trim();
//            if (!part.isEmpty()) {
//                try {
//                    tien[i] = Double.parseDouble(part);
//                } catch (NumberFormatException e) {
//                    throw new IllegalArgumentException("Tiền không hợp lệ ở vị trí " + (i + 1) + ": " + part);
//                }
//            }
//        }
//
//        return tien;
//    }
//    // Xác định có phải lô thượng hay không (tùy miền)
//    public boolean isLoThuong(String mien, String giai) {
//        String g = giai.toUpperCase();
//        String m = mien.toUpperCase();
//
//        if (m.contains("BẮC")) {
//            return g.equals("G5") || g.equals("G6") || g.equals("ĐẶC BIỆT");
//        } else if (m.contains("NAM") || m.contains("TRUNG")) {
//            return g.equals("G5") || g.equals("G6") || g.equals("G7") || g.equals("ĐẶC BIỆT");
//        }
//        return false;
//    }
//    private boolean chuaDu3ChuSo(String soTrung, String soNguoiChoi) {
//        if (soTrung == null || soTrung.length() < 3 || soNguoiChoi.length() != 3) return false;
//        String baSoCuoi = soTrung.substring(soTrung.length() - 3);
//        return baSoCuoi.equals(soNguoiChoi);
//    }
//
//    @SuppressWarnings("unchecked")
//    private <T> T getField(Object obj, String methodName) {
//        try {
//            Method method = obj.getClass().getMethod(methodName);
//            return (T) method.invoke(obj);
//        } catch (Exception e) {
//            throw new RuntimeException("Lỗi lấy field: " + methodName, e);
//        }
//    }
//    public String chuanHoaTenGiai(String giaiRaw) {
//        if (giaiRaw == null) return "";
//        String giai = giaiRaw.trim().toUpperCase()
//                .replace(".", "")
//                .replace("GIẢI", "")
//                .replace("GIAI", "")
//                .replace(" ", "");
//
//        if (giai.contains("DB") || giai.contains("DACBIET") || giai.contains("ĐẶCBIỆT")) {
//            return "ĐẶC BIỆT";
//        }
//
//        if (giai.matches("G[1-8]")) return giai;
//
//        return switch (giai) {
//            case "BAY", "BẢY" -> "G7";
//            case "SAU", "SÁU" -> "G6";
//            case "NAM", "NĂM" -> "G5";
//            case "BON", "BỐN" -> "G4";
//            case "BA" -> "G3";
//            case "HAI" -> "G2";
//            case "NHAT", "NHẤT" -> "G1";
//            default -> giaiRaw.toUpperCase();
//        };
//    }
//
//}






package com.example.doxoso.service;
import com.example.doxoso.model.DoiChieuKetQuaDto;
import com.example.doxoso.model.SoNguoiChoi;
import com.example.doxoso.repository.KetQuaMienBacRepository;
import com.example.doxoso.repository.KetQuaMienNamRepository;
import com.example.doxoso.repository.KetQuaMienTrungRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

@Service
public class BaChanService {

    @Autowired
    private KetQuaMienBacRepository bacRepo;

    @Autowired
    private KetQuaMienTrungRepository trungRepo;

    @Autowired
    private KetQuaMienNamRepository namRepo;

    public DoiChieuKetQuaDto xuLyBaChan(SoNguoiChoi so) {
        DoiChieuKetQuaDto dto = new DoiChieuKetQuaDto();
        dto.setSoDanh(so.getSoDanh());
        dto.setCachTrung("3 chân");

        LocalDate ngay = so.getNgay();

        // Gộp kết quả từ 3 miền
        List<Object> ketQuaTongHop = new ArrayList<>();
        String mien = so.getMien().toUpperCase();

        if (mien.contains("BẮC")) {
            ketQuaTongHop.addAll(bacRepo.findAllByNgay(ngay));
        }
        if (mien.contains("TRUNG")) {
            ketQuaTongHop.addAll(trungRepo.findAllByNgay(ngay));
        }
        if (mien.contains("NAM")) {
            ketQuaTongHop.addAll(namRepo.findAllByNgay(ngay));
        }


        // Trích ra danh sách đài duy nhất từ kết quả
        Set<String> danhSachDai = ketQuaTongHop.stream()
                .map(kq -> (String) getField(kq, "getTenDai"))
                .filter(Objects::nonNull)
                .map(String::trim)
                .map(String::toUpperCase)
                .collect(Collectors.toSet());

        AtomicBoolean daTrung = new AtomicBoolean(false);

        List<DoiChieuKetQuaDto.KetQuaTheoDai> danhSachKetQua = danhSachDai.stream().map(tenDai -> {
            DoiChieuKetQuaDto.KetQuaTheoDai ketQuaDai = new DoiChieuKetQuaDto.KetQuaTheoDai();
            ketQuaDai.setTenDai(tenDai);
            ketQuaDai.setMien(xacDinhMienCuaDai(tenDai, ngay));

            List<String> giaiTrung = ketQuaTongHop.stream()
                    .filter(kq -> tenDai.equalsIgnoreCase((String) getField(kq, "getTenDai")))
                    .filter(kq -> {
                        String soTrung = (String) getField(kq, "getSoTrung");
                        // kiểm tra số có đúngk hông ?
                        if (soTrung != null && soTrung.length() >= 3) {
                            return soTrung.substring(soTrung.length() - 3).equals(so.getSoDanh());
                        }
                        if (so.getSoDanh() == null || so.getSoDanh().length() != 3) {
                            dto.setTrung(false);
                            dto.setSaiLyDo(List.of("Số đánh không hợp lệ"));
                        }
                        return false;
                    })
                    .map(kq -> (String) getField(kq, "getGiai"))
                    .collect(Collectors.toList());

            if (!giaiTrung.isEmpty()) {
                ketQuaDai.setTrung(true);
                ketQuaDai.setSoTrung(so.getSoDanh());
                ketQuaDai.setGiaiTrung(giaiTrung);
                ketQuaDai.setSoLanTrung(giaiTrung.size());
                daTrung.set(true);
            } else {
                ketQuaDai.setTrung(false);
                ketQuaDai.setLyDo("Không có số trúng " + so.getSoDanh());
                ketQuaDai.setSoLanTrung(0);
            }

            return ketQuaDai;
        }).collect(Collectors.toList());

        dto.setTrung(daTrung.get());
        dto.setKetQuaTungDai(danhSachKetQua);
        dto.setDanhSachDai(danhSachKetQua.stream()
                .map(DoiChieuKetQuaDto.KetQuaTheoDai::getTenDai)
                .collect(Collectors.toList()));
        return dto;
    }

    // ✅ Hàm phản xạ để gọi getTenDai, getSoTrung, getGiai
    private <T> T getField(Object obj, String methodName) {
        try {
            return (T) obj.getClass().getMethod(methodName).invoke(obj);
        } catch (Exception e) {
            throw new RuntimeException("Lỗi phản xạ khi lấy field: " + methodName);
        }
    }

    // ✅ Xác định miền từ tên đài (dò từ các repo)
    private String xacDinhMienCuaDai(String tenDai, LocalDate ngay) {
        if (bacRepo.findAllByNgay(ngay).stream()
                .anyMatch(kq -> tenDai.equalsIgnoreCase((String) getField(kq, "getTenDai")))) {
            return "MIỀN BẮC";
        }
        if (trungRepo.findAllByNgay(ngay).stream()
                .anyMatch(kq -> tenDai.equalsIgnoreCase((String) getField(kq, "getTenDai")))) {
            return "MIỀN TRUNG";
        }
        if (namRepo.findAllByNgay(ngay).stream()
                .anyMatch(kq -> tenDai.equalsIgnoreCase((String) getField(kq, "getTenDai")))) {
            return "MIỀN NAM";
        }
        return "KHÔNG RÕ";
    }


}









//package com.example.doxoso.service;
//
//import com.example.doxoso.model.DoiChieuKetQuaDto;
//import com.example.doxoso.model.SoNguoiChoi;
//import com.example.doxoso.repository.KetQuaMienBacRepository;
//import com.example.doxoso.repository.KetQuaMienNamRepository;
//import com.example.doxoso.repository.KetQuaMienTrungRepository;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//import java.time.LocalDate;
//import java.util.*;
//import java.util.stream.Collectors;
//
//@Service
//public class BaChanService {
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
//    public DoiChieuKetQuaDto xuLyBaChan(SoNguoiChoi so) {
//        DoiChieuKetQuaDto dto = new DoiChieuKetQuaDto();
//        dto.setSoDanh(so.getSoDanh());
//        dto.setCachTrung("3 chân");
//
//        // 1️⃣ Kiểm tra số đánh hợp lệ
//        if (!kiemTraSoDanhHopLe(so.getSoDanh())) {
//            dto.setTrung(false);
//            dto.setSaiLyDo(List.of("Số đánh không hợp lệ"));
//            return dto;
//        }
//
//        LocalDate ngay = so.getNgay();
//
//        // 2️⃣ Lấy kết quả từ các miền theo ngày (query 1 lần mỗi miền)
//        List<Object> ketQuaTongHop = layKetQuaTheoMien(so.getMien(), ngay);
//
//        // 3️⃣ Nhóm kết quả theo tên đài
//        Map<String, List<Object>> ketQuaTheoDai = ketQuaTongHop.stream()
//                .collect(Collectors.groupingBy(
//                        kq -> ((String) callGetter(kq, "getTenDai")).trim().toUpperCase()
//                ));
//
//        boolean coTrung = false;
//        List<DoiChieuKetQuaDto.KetQuaTheoDai> danhSachKetQua = new ArrayList<>();
//
//        // 4️⃣ Duyệt từng đài và tìm giải trúng
//        for (Map.Entry<String, List<Object>> entry : ketQuaTheoDai.entrySet()) {
//            String tenDai = entry.getKey();
//            List<Object> ketQuaDai = entry.getValue();
//
//            List<String> giaiTrung = ketQuaDai.stream()
//                    .filter(kq -> {
//                        String soTrung = (String) callGetter(kq, "getSoTrung");
//                        return soTrung != null
//                                && soTrung.length() >= 3
//                                && soTrung.substring(soTrung.length() - 3).equals(so.getSoDanh());
//                    })
//                    .map(kq -> (String) callGetter(kq, "getGiai"))
//                    .collect(Collectors.toList());
//
//            DoiChieuKetQuaDto.KetQuaTheoDai kqDai = new DoiChieuKetQuaDto.KetQuaTheoDai();
//            kqDai.setTenDai(tenDai);
//            kqDai.setMien(xacDinhMienCuaDai(tenDai, ketQuaTongHop));
//            kqDai.setSoLanTrung(giaiTrung.size());
//
//            if (!giaiTrung.isEmpty()) {
//                kqDai.setTrung(true);
//                kqDai.setSoTrung(so.getSoDanh());
//                kqDai.setGiaiTrung(giaiTrung);
//                coTrung = true;
//            } else {
//                kqDai.setTrung(false);
//                kqDai.setLyDo("Không có số trúng " + so.getSoDanh());
//            }
//
//            danhSachKetQua.add(kqDai);
//        }
//
//        // 5️⃣ Hoàn thiện DTO
//        dto.setTrung(coTrung);
//        dto.setKetQuaTungDai(danhSachKetQua);
//        dto.setDanhSachDai(danhSachKetQua.stream()
//                .map(DoiChieuKetQuaDto.KetQuaTheoDai::getTenDai)
//                .collect(Collectors.toList()));
//
//        return dto;
//    }
//
//    // ====== HÀM PHỤ TRỢ ======
//
//    private boolean kiemTraSoDanhHopLe(String soDanh) {
//        return soDanh != null && soDanh.matches("\\d{3}");
//    }
//
//    private List<Object> layKetQuaTheoMien(String mien, LocalDate ngay) {
//        List<Object> ketQua = new ArrayList<>();
//        String mienUpper = mien.toUpperCase();
//
//        if (mienUpper.contains("BẮC")) {
//            ketQua.addAll(bacRepo.findAllByNgay(ngay));
//        }
//        if (mienUpper.contains("TRUNG")) {
//            ketQua.addAll(trungRepo.findAllByNgay(ngay));
//        }
//        if (mienUpper.contains("NAM")) {
//            ketQua.addAll(namRepo.findAllByNgay(ngay));
//        }
//        return ketQua;
//    }
//    /**
//     * Xác định miền (MB, MT, MN) dựa vào tên đài.
//     */
//    private String xacDinhMienCuaDai(String tenDai, List<Object> ketQuaTongHop) {
//        if (tenDai == null || tenDai.isBlank()) {
//            return "KHÔNG XÁC ĐỊNH"; // Fallback nếu dữ liệu rỗng
//        }
//
//        String ten = tenDai.trim().toUpperCase();
//        if (ten.contains("MIỀN BẮC")) return "MB";
//        if (ten.contains("MIỀN TRUNG")) return "MT";
//        if (ten.contains("MIỀN NAM")) return "MN";
//        return "KHÁC";
//    }
//
//
//    /**
//     * Gọi phương thức getter bằng Reflection.
//     *
//     * @param obj Đối tượng chứa getter
//     * @param methodName Tên phương thức getter (ví dụ: "getTenDai")
//     * @return Giá trị trả về từ getter (có thể null nếu gọi phiên bản 2 tham số)
//     */
//    private Object callGetter(Object obj, String methodName) {
//        return callGetter(obj, methodName, Object.class, true); // Cho phép null để tương thích code cũ
//    }
//
//    /**
//     * Gọi getter với kiểm tra kiểu dữ liệu.
//     *
//     * @param obj Đối tượng chứa getter
//     * @param methodName Tên phương thức getter
//     * @param expectedType Kiểu mong đợi của giá trị trả về
//     * @return Giá trị trả về từ getter (không được null, đúng kiểu expectedType)
//     */
//    private <T> T callGetter(Object obj, String methodName, Class<T> expectedType) {
//        return expectedType.cast(callGetter(obj, methodName, expectedType, false)); // Không cho phép null
//    }
//
//    /**
//     * Hàm lõi xử lý logic gọi getter.
//     */
//    private Object callGetter(Object obj, String methodName, Class<?> expectedType, boolean allowNull) {
//        if (obj == null) {
//            throw new IllegalArgumentException("Đối tượng truyền vào bị null");
//        }
//        if (methodName == null || methodName.isBlank()) {
//            throw new IllegalArgumentException("Tên phương thức getter không hợp lệ");
//        }
//        if (expectedType == null) {
//            throw new IllegalArgumentException("Kiểu mong đợi không được null");
//        }
//
//        try {
//            var method = obj.getClass().getMethod(methodName);
//            Object value = method.invoke(obj);
//
//            if (value == null) {
//                if (allowNull) return null;
//                throw new IllegalStateException("Giá trị trả về từ " + methodName + " là null");
//            }
//            if (!expectedType.isInstance(value)) {
//                throw new IllegalArgumentException("Giá trị từ " + methodName
//                        + " không đúng kiểu: " + expectedType.getSimpleName()
//                        + ", thực tế: " + value.getClass().getSimpleName());
//            }
//            return value;
//
//        } catch (NoSuchMethodException e) {
//            throw new RuntimeException("Không tìm thấy phương thức: " + methodName, e);
//        } catch (Exception e) {
//            throw new RuntimeException("Lỗi khi gọi getter: " + methodName, e);
//        }
//    }
//
//
//}
