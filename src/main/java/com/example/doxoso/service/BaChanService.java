
package com.example.doxoso.service;

import com.example.doxoso.model.DoiChieuKetQuaDto;
import org.springframework.stereotype.Service;

import java.lang.reflect.Method;
import java.util.List;

@Service
public class BaChanService {

    // Hàm chính để xử lý 3 chân
    public void xuLyBaChan(DoiChieuKetQuaDto dto, String soDanh, String tienChuoi, String mien, List<Object> danhSachKetQua) {
        double[] tien = tachTienDanhBaChan(tienChuoi);
        double tienBaoLo = tien[0];
        double tienThuong = tien[1];
        double tienDacBiet = tien[2];

        double maxTongTien = 0;
        String loaiTrungMax = null;
        String giaiTrungMax = null;

        double tienBaoLoMax = 0;
        double tienThuongMax = 0;
        double tienDacBietMax = 0;

        String mienChuan = mien.trim().toUpperCase();


        for (Object kq : danhSachKetQua) {
            String soTrung = getField(kq, "getSoTrung");
            String giaiRaw = getField(kq, "getGiai");
            String giai = chuanHoaTenGiai(giaiRaw);

            if (soTrung == null || !chuaDu3ChuSo(soTrung, soDanh)) continue;

            double tienBL = 0, tienT = 0, tienDB = 0;
            String loaiTrung = "";
            // tính tiền đặc biệt
            if ("ĐẶC BIỆT".equalsIgnoreCase(giai)) {
                if (mienChuan.contains("BẮC")) {
                    tienBL = tienBaoLo * 600 / 23;
                    tienT = tienThuong * 600 / 10;
                    tienDB = tienDacBiet * 600;
                } else if (mienChuan.contains("TRUNG")) {
                    tienBL = tienBaoLo * 600 / 17;
                    tienT = tienThuong * 100;
                    tienDB = tienDacBiet * 600;
                } else if (mienChuan.contains("NAM")) {
                    tienBL = tienBaoLo * 600 / 17;
                    tienT = tienThuong * 100;
                    tienDB = tienDacBiet * 600;
                }
                loaiTrung = "Đặc biệt";
                //tính tiền thượng
            } else if (isLoThuong(mien, giai)) {
                if (mienChuan.contains("BẮC")) {
                    tienBL = tienBaoLo * 600 / 23;
                    tienT = tienThuong * 600 / 10;
                } else if (mienChuan.contains("TRUNG")) {
                    tienBL = tienBaoLo * 600 / 17;
                    tienT = tienThuong * 100;
                } else if (mienChuan.contains("NAM")) {
                    tienBL = tienBaoLo * 600 / 17;
                    tienT = tienThuong * 100;
                }
                loaiTrung = "Lô thượng";
                // tính tiền lô
            } else {

                if (mienChuan.contains("BẮC")) {
                    tienBL = tienBaoLo * 600 / 23;
                } else if (mienChuan.contains("TRUNG")) {
                    tienBL = tienBaoLo * 600 / 17;
                } else if (mienChuan.contains("NAM")) {
                    tienBL = tienBaoLo * 600 / 17;
                }
                loaiTrung = "Bao lô";
            }

            double tongTien = tienBL + tienT + tienDB;

            if (tongTien > maxTongTien) {
                maxTongTien = tongTien;
                loaiTrungMax = loaiTrung;
                giaiTrungMax = giai;

                tienBaoLoMax = tienBL;
                tienThuongMax = tienT;
                tienDacBietMax = tienDB;
            }
        }

        if (maxTongTien > 0) {
            dto.setTrung(true);

            dto.setCachTrung(loaiTrungMax);
            dto.setGiaiTrung(giaiTrungMax);
// làm tròn 2 số thập phân
            dto.setTienTrung(Math.round(maxTongTien));
            dto.setTienTrungBaoLo((double) Math.round(tienBaoLoMax));
            dto.setTienTrungThuong((double) Math.round(tienThuongMax));
            dto.setTienTrungDacBiet((double) Math.round(tienDacBietMax));



        } else {
            dto.setTrung(false);
            dto.setTienTrung(0);
            dto.setSaiLyDo(List.of("Sai số"));
        }
    }
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
//        for (Object kq : danhSachKetQua) {
//            String soTrung = getField(kq, "getSoTrung");
//            String giaiRaw = getField(kq, "getGiai");
//            String giai = chuanHoaTenGiai(giaiRaw);
//
//            // Kiểm tra số trúng có đúng định dạng không
//            if (soTrung == null || !chuaDu3ChuSo(soTrung, soDanh)) continue;
//
//            double tienBL = 0, tienT = 0, tienDB = 0;
//            String loaiTrung = "";
//
//            // Tính tiền cho từng loại giải
//            if ("ĐẶC BIỆT".equalsIgnoreCase(giai)) {
//                // Tính tiền theo miền
//                tienBL = calculateTienBaoLo(tienBaoLo, mienChuan);
//                tienT = tienThuong * (mienChuan.contains("BẮC") ? 60 : 100);
//                tienDB = tienDacBiet * 600;
//                loaiTrung = "Đặc biệt";
//            } else if (isLoThuong(mien, giai)) {
//                tienBL = calculateTienBaoLo(tienBaoLo, mienChuan);
//                tienT = tienThuong * (mienChuan.contains("BẮC") ? 60 : 100);
//                loaiTrung = "Lô thượng";
//            } else {
//                tienBL = calculateTienBaoLo(tienBaoLo, mienChuan);
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
//            dto.setCachTrung(loaiTrungMax);
//            dto.setGiaiTrung(giaiTrungMax);
//            dto.setTienTrung(Math.round(maxTongTien));
//            dto.setTienTrungBaoLo((double) Math.round(tienBaoLoMax));
//            dto.setTienTrungThuong((double) Math.round(tienThuongMax));
//            dto.setTienTrungDacBiet((double) Math.round(tienDacBietMax));
//        } else {
//            dto.setTrung(false);
//            dto.setTienTrung(0);
//            dto.setSaiLyDo(List.of("Sai số"));
//        }
//    }

    // Hàm tính tiền bao lô theo miền
//    private double calculateTienBaoLo(double tienBaoLo, String mienChuan) {
//        switch (mienChuan) {
//            case "BẮC":
//                return tienBaoLo * 600 / 23;
//            case "TRUNG":
//            case "NAM":
//                return tienBaoLo * 600 / 17;
//            default:
//                return 0;
//        }
//    }



    // Hàm phụ tách tiền, cho phép thiếu hoặc rỗng → gán 0
//    public double[] tachTienDanhBaChan(String tienDanh) {
//        double[] tien = new double[] {0.0, 0.0, 0.0}; // [baoLo, thuong, dacBiet]
//
//        if (tienDanh == null || tienDanh.trim().isEmpty()) return tien;
//
//        String[] parts = tienDanh.trim().split("-");
//
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
    public double[] tachTienDanhBaChan(String tienDanh) {
        double[] tien = new double[] {0.0, 0.0, 0.0}; // [baoLo, thuong, dacBiet]

        if (tienDanh == null || tienDanh.trim().isEmpty()) return tien;

        String[] parts = tienDanh.trim().split("-");
        for (int i = 0; i < Math.min(3, parts.length); i++) {
            String part = parts[i].trim();
            if (!part.isEmpty()) {
                try {
                    tien[i] = Double.parseDouble(part);
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException("Tiền không hợp lệ ở vị trí " + (i + 1) + ": " + part);
                }
            }
        }

        return tien;
    }
    // Xác định có phải lô thượng hay không (tùy miền)
    public boolean isLoThuong(String mien, String giai) {
        String g = giai.toUpperCase();
        String m = mien.toUpperCase();

        if (m.contains("BẮC")) {
            return g.equals("G5") || g.equals("G6") || g.equals("ĐẶC BIỆT");
        } else if (m.contains("NAM") || m.contains("TRUNG")) {
            return g.equals("G5") || g.equals("G6") || g.equals("G7") || g.equals("ĐẶC BIỆT");
        }
        return false;
    }
    private boolean chuaDu3ChuSo(String soTrung, String soNguoiChoi) {
        if (soTrung == null || soTrung.length() < 3 || soNguoiChoi.length() != 3) return false;
        String baSoCuoi = soTrung.substring(soTrung.length() - 3);
        return baSoCuoi.equals(soNguoiChoi);
    }

    @SuppressWarnings("unchecked")
    private <T> T getField(Object obj, String methodName) {
        try {
            Method method = obj.getClass().getMethod(methodName);
            return (T) method.invoke(obj);
        } catch (Exception e) {
            throw new RuntimeException("Lỗi lấy field: " + methodName, e);
        }
    }
    public String chuanHoaTenGiai(String giaiRaw) {
        if (giaiRaw == null) return "";
        String giai = giaiRaw.trim().toUpperCase()
                .replace(".", "")
                .replace("GIẢI", "")
                .replace("GIAI", "")
                .replace(" ", "");

        if (giai.contains("DB") || giai.contains("DACBIET") || giai.contains("ĐẶCBIỆT")) {
            return "ĐẶC BIỆT";
        }

        if (giai.matches("G[1-8]")) return giai;

        return switch (giai) {
            case "BAY", "BẢY" -> "G7";
            case "SAU", "SÁU" -> "G6";
            case "NAM", "NĂM" -> "G5";
            case "BON", "BỐN" -> "G4";
            case "BA" -> "G3";
            case "HAI" -> "G2";
            case "NHAT", "NHẤT" -> "G1";
            default -> giaiRaw.toUpperCase();
        };
    }

}
