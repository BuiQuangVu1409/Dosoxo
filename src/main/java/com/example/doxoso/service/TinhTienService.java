////package com.example.doxoso.service;
////
////import com.example.doxoso.model.KetQuaMienBac;
////import com.example.doxoso.repository.KetQuaMienBacRepository;
////import com.example.doxoso.repository.KetQuaMienNamRepository;
////import com.example.doxoso.repository.KetQuaMienTrungRepository;
////import org.springframework.beans.factory.annotation.Autowired;
////import org.springframework.stereotype.Service;
////
////import java.lang.reflect.Method;
////import java.text.Normalizer;
////import java.time.LocalDate;
////import java.util.ArrayList;
////import java.util.List;
////import java.util.Objects;
////import java.util.regex.Pattern;
////
////@Service
////public class TinhTienService implements ITinhTienService {
////    @Autowired
////    KetQuaMienBacRepository bacRepo;
////    @Autowired
////    KetQuaMienNamRepository namRepo;
////    @Autowired
////    KetQuaMienTrungRepository trungRepo;
////    public double tinhTienTrung(String cachDanh, String tienDanh, String mien) {
////        String loai = removeDiacritics(cachDanh).toUpperCase().replaceAll("\\s+", "");
////
////        if (loai.equals("3CHAN")) return 0; // đã xử lý riêng
////
////        double tienDanhDouble;
////        try {
////            tienDanhDouble = Double.parseDouble(tienDanh);
////        } catch (NumberFormatException e) {
////            throw new IllegalArgumentException("Giá trị tiền không hợp lệ: " + tienDanh, e);
////        }
////
////        return switch (loai) {
////            //tính tiền xuyên
////            case "XUYEN2" -> {
////                String m = removeDiacritics(mien).toUpperCase();
////                if (m.contains("BAC")) {
////                    yield tienDanhDouble * 10;
////                } else if (m.contains("TRUNG")) {
////                    yield tienDanhDouble * 15;
////                } else if (m.contains("NAM")) {
////                    yield tienDanhDouble * 15;
////                } else {
////                    yield 0;
////                }
////            }
////            case "XUYEN3" -> {
////                String m = removeDiacritics(mien).toUpperCase();
////                if (m.contains("BAC")) {
////                    yield tienDanhDouble * 40;
////                } else if (m.contains("TRUNG")) {
////                    yield tienDanhDouble * 60;
////                } else if (m.contains("NAM")) {
////                    yield tienDanhDouble * 60;
////                } else {
////                    yield 0;
////                }
////            }
////            case "XUYEN4" -> {
////                String m = removeDiacritics(mien).toUpperCase();
////                if (m.contains("BAC")) {
////                    yield tienDanhDouble * 100;
////                } else if (m.contains("TRUNG")) {
////                    yield tienDanhDouble * 120;
////                } else if (m.contains("NAM")) {
////                    yield tienDanhDouble * 120;
////                } else {
////                    yield 0;
////                }
////            }
////            case "XUYEN5" -> {
////                String m = removeDiacritics(mien).toUpperCase();
////                if (m.contains("BAC")) {
////                    yield tienDanhDouble * 200;
////                } else if (m.contains("TRUNG")) {
////                    yield tienDanhDouble * 250;
////                } else if (m.contains("NAM")) {
////                    yield tienDanhDouble * 250;
////                } else {
////                    yield 0;
////                }
////            }
////
////
////            // tính tiền 2 chân
////            case "2CHAN" -> {
////                String m = removeDiacritics(mien).toUpperCase().trim();
////                if (m.contains("BAC")) {
////                    yield tienDanhDouble * 70 / 27 ;
////                } else if (m.contains("TRUNG")) {
////                    yield tienDanhDouble * 70 / 18;
////                } else if (m.contains("NAM")) {
////                    yield tienDanhDouble * 70/ 18 ;
////                } else {
////                    yield 0;
////                }
////            }
////
//////            case "DAUMIENBAC" -> tienDanhDouble * 70 / 4 ;
//////            case "DAUMIENTRUNG", "DAUMIENNAM" -> tienDanhDouble * 70;
//////            case "DUOIMIENBAC" -> tienDanhDouble * 70;
//////            case "DUOIMIENTRUNG", "DUOIMIENNAM" -> tienDanhDouble * 70;
//////            case "DAUDUOIMIENBAC" -> tienDanhDouble * 70 / 5;
//////            case "DAUDUOIMIENTRUNG", "DAUDUOIMIENNAM" -> tienDanhDouble * 70 / 2 ;
////
////            case "DAU" -> {
////                String m = removeDiacritics(mien).toUpperCase();
////                if (m.contains("BAC")) {
////                    yield tienDanhDouble * 70 / 4;
////                } else if (m.contains("TRUNG")) {
////                    yield tienDanhDouble * 70;
////                } else if (m.contains("NAM")) {
////                    yield tienDanhDouble * 70;
////                } else {
////                    yield 0;
////                }
////            }
////            // ĐUÔI
////            case "DUOI" -> {
////                String m = removeDiacritics(mien).toUpperCase();
////                if (m.contains("BAC")) {
////                    yield tienDanhDouble * 70;
////                } else if (m.contains("TRUNG")) {
////                    yield tienDanhDouble * 70;
////                } else if (m.contains("NAM")) {
////                    yield tienDanhDouble * 70;
////                } else {
////                    yield 0;
////                }
////            }
////
////            // ĐẦU ĐUÔI
////            case "DAUDUOI" -> {
////                String m = removeDiacritics(mien).toUpperCase();
////                if (m.contains("BAC")) {
////                    yield tienDanhDouble * 70 / 5;
////                } else if (m.contains("TRUNG")) {
////                    yield tienDanhDouble * 70 / 2;
////                } else if (m.contains("NAM")) {
////                    yield tienDanhDouble * 70 / 2;
////                } else {
////                    yield 0;
////                }
////            }
////            // LỚN , nhỏ
////            case "LON", "NHO" -> {
////                String m = removeDiacritics(mien).toUpperCase();
////                if (m.contains("BAC")) {
////                    yield tienDanhDouble * 1.95;
////                } else if (m.contains("TRUNG") || m.contains("NAM")) {
////                    yield tienDanhDouble * 1.95;
////                } else {
////                    yield 0;
////                }
////            }
////
////            default -> 0;
////        };
////    }
////    public double tinhTienDauDuoi(boolean trungDau, boolean trungDuoi, String mien, double tienDanh) {
////        String m = removeDiacritics(mien).toUpperCase();
////
////        if (trungDau && trungDuoi) {
////            // Trúng cả ĐẦU và ĐUÔI → dùng công thức như bạn đã có
////            if (m.contains("BAC")) return tienDanh * 70 / 5;
////            if (m.contains("TRUNG") || m.contains("NAM")) return tienDanh * 70 / 2;
////        } else if (trungDau) {
////            // Trúng ĐẦU (cách đánh ĐẦU ĐUÔI)
////            return tienDanh * 500; // hệ số riêng cho ĐẦU trong DAUDUOI
////        } else if (trungDuoi) {
////            // Trúng ĐUÔI (cách đánh ĐẦU ĐUÔI)
////            return tienDanh * 35;  // hệ số riêng cho ĐUÔI trong DAUDUOI
////        }
////
////        return 0;
////    }
////
////
////
////    private String removeDiacritics(String input) {
////        String normalized = Normalizer.normalize(input, Normalizer.Form.NFD);
////        return Pattern.compile("\\p{InCombiningDiacriticalMarks}+")
////                .matcher(normalized)
////                .replaceAll("");
////    }
////
////}
//////    private double tinhTienLon(double tien, String mien) {
//////        String m = removeDiacritics(mien).toUpperCase();
//////        if (m.contains("BAC")) return tien * 1.8;
//////        if (m.contains("TRUNG") || m.contains("NAM")) return tien * 2;
//////        return 0;
//////    }
//////
//////    private double tinhTienNho(double tien, String mien) {
//////        return tinhTienLon(tien, mien); // vì logic giống nhau
//////    }
//////case "LON" -> tinhTienLon(tienDanhDouble, mien);
//////        case "NHO" -> tinhTienNho(tienDanhDouble, mien);
//
//
//
//
//
//
//
//
//
//
//
//
//
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
//import java.util.List;
//import java.util.regex.Pattern;
//
//@Service
//public class TinhTienService implements ITinhTienService {
//    @Autowired
//    KetQuaMienBacRepository bacRepo;
//    @Autowired
//    KetQuaMienNamRepository namRepo;
//    @Autowired
//    KetQuaMienTrungRepository trungRepo;
//
//    public double tinhTienTrung(String cachDanh, String tienDanh, String mien) {
//        String loai = removeDiacritics(cachDanh).toUpperCase().replaceAll("\\s+", "");
//        double tienDanhDouble;
//
//        if (loai.equals("3CHAN")) return 0; // đã xử lý riêng
//
//        try {
//            tienDanhDouble = Double.parseDouble(tienDanh);
//        } catch (NumberFormatException e) {
//            throw new IllegalArgumentException("Giá trị tiền không hợp lệ: " + tienDanh, e);
//        }
//
//        return switch (loai) {
//            case "XUYEN2" -> getHeSoXuyen(2, tienDanhDouble, mien);
//            case "XUYEN3" -> getHeSoXuyen(3, tienDanhDouble, mien);
//            case "XUYEN4" -> getHeSoXuyen(4, tienDanhDouble, mien);
//            case "XUYEN5" -> getHeSoXuyen(5, tienDanhDouble, mien);
//            case "2CHAN"   -> getHeSo2Chan(tienDanhDouble, mien);
//            case "DAU"     -> getHeSoDau(tienDanhDouble, mien);
//            case "DUOI"    -> getHeSoDuoi(tienDanhDouble, mien);
//            case "DAUDUOI" -> getHeSoDauDuoiCaCap(tienDanhDouble, mien);
//            case "LON", "NHO" -> getHeSoLonNho(tienDanhDouble, mien);
//            default -> 0;
//        };
//    }
//
//    public double tinhTienDauDuoi(boolean trungDau, boolean trungDuoi,
//                                  String mienTrungDau, String mienTrungDuoi,
//                                  double tienDanh) {
//        double tien = 0;
//
//        if (trungDau && trungDuoi) {
//            // Nếu cả hai cùng trúng và cùng miền → xử lý gộp
//            if (mienTrungDau.equals(mienTrungDuoi)) {
//                tien += getHeSoDauDuoiCaCap(tienDanh, mienTrungDau);
//            } else {
//                // Trúng cả 2 nhưng khác miền → tính riêng
//                tien += tinhTienDau(trungDau, mienTrungDau, tienDanh / 2);
//                tien += tinhTienDuoi(trungDuoi, mienTrungDuoi, tienDanh / 2);
//            }
//        } else if (trungDau) {
//            tien += tinhTienDau(true, mienTrungDau, tienDanh);
//        } else if (trungDuoi) {
//            tien += tinhTienDuoi(true, mienTrungDuoi, tienDanh);
//        }
//
//        return tien;
//    }
//
//
//    // === HÀM PHỤ ===
//    private double tinhTienDau(boolean trungDau, String mien, double tienDanh) {
//        if (!trungDau) return 0;
//        switch (mien) {
//            case "MIỀN BẮC":
//                return tienDanh * 70/4;
//            case "MIỀN TRUNG":
//                return tienDanh * 70;
//            case "MIỀN NAM":
//                return tienDanh * 70;
//            default:
//                return 0;
//        }
//    }
//
//    private double tinhTienDuoi(boolean trungDuoi, String mien, double tienDanh) {
//        if (!trungDuoi) return 0;
//        switch (mien) {
//            case "MIỀN BẮC":
//                return tienDanh * 70;
//            case "MIỀN TRUNG":
//                return tienDanh * 70;
//            case "MIỀN NAM":
//                return tienDanh * 70;
//            default:
//                return 0;
//        }
//    }
//    private double[] tachTienDanhBaChan(String tienChuoi) {
//        double[] tien = new double[] {0.0, 0.0, 0.0}; // [baoLo, thuong, dacBiet]
//        if (tienChuoi == null || tienChuoi.trim().isEmpty()) return tien;
//
//        String[] parts = tienChuoi.trim().split("-");
//        for (int i = 0; i < Math.min(3, parts.length); i++) {
//            try {
//                tien[i] = Double.parseDouble(parts[i].trim());
//            } catch (NumberFormatException ignored) {}
//        }
//
//        return tien;
//    }
//
//    private double getHeSoBaoLo(String mien, double tien) {
//        if (mien.contains("BAC")) return tien * 600 / 23;
//        if (mien.contains("TRUNG") || mien.contains("NAM")) return tien * 600 / 17;
//        return 0;
//    }
//
//    private double getHeSoThuong(String mien, double tien) {
//        if (mien.contains("BAC")) return tien * 600 / 10;
//        if (mien.contains("TRUNG") || mien.contains("NAM")) return tien * 100;
//        return 0;
//    }
//
//    private boolean isLoThuong(String mien, String giai) {
//        return switch (mien) {
//            case "BAC" -> giai.equals("G5") || giai.equals("G6") || giai.equals("ĐẶC BIỆT");
//            case "TRUNG", "NAM" -> giai.equals("G5") || giai.equals("G6") || giai.equals("G7") || giai.equals("ĐẶC BIỆT");
//            default -> false;
//        };
//    }
//
//    public double tinhTienBaChan(String tienChuoi, String mien, List<DoiChieuKetQuaDto.KetQuaTheoDai> ketQuaTheoDai) {
//        double[] tien = tachTienDanhBaChan(tienChuoi); // [baoLo, thuong, dacBiet]
//        double tienBaoLo = tien[0];
//        double tienThuong = tien[1];
//        double tienDacBiet = tien[2];
//
//        double tongTien = 0;
//
//        for (DoiChieuKetQuaDto.KetQuaTheoDai dai : ketQuaTheoDai) {
//            if (!dai.isTrung()) continue;
//
//            String tenMien = removeDiacritics(dai.getMien()).toUpperCase();
//
//            for (String giai : dai.getGiai()) {
//                String g = giai.trim().toUpperCase();
//
//                double tienBL = 0, tienT = 0, tienDB = 0;
//
//                if (g.equals("ĐẶC BIỆT")) {
//                    tienBL = getHeSoBaoLo(tenMien, tienBaoLo);
//                    tienT = getHeSoThuong(tenMien, tienThuong);
//                    tienDB = tienDacBiet * 600;
//                } else if (isLoThuong(tenMien, g)) {
//                    tienBL = getHeSoBaoLo(tenMien, tienBaoLo);
//                    tienT = getHeSoThuong(tenMien, tienThuong);
//                } else {
//                    tienBL = getHeSoBaoLo(tenMien, tienBaoLo);
//                }
//
//                tongTien += tienBL + tienT + tienDB;
//            }
//        }
//
//        return tongTien;
//    }
//
//
//
//    private double getHeSoXuyen(int soXuyen, double tien, String mien) {
//        String m = removeDiacritics(mien).toUpperCase();
//        int heSo = 0;
//
//        if (soXuyen == 2) {
//            if (m.contains("BAC")) heSo = 10;
//            else if (m.contains("TRUNG")) heSo = 15;
//            else if (m.contains("NAM")) heSo = 15;
//        } else if (soXuyen == 3) {
//            if (m.contains("BAC")) heSo = 40;
//            else if (m.contains("TRUNG")) heSo = 60;
//            else if (m.contains("NAM")) heSo = 60;
//        } else if (soXuyen == 4) {
//            if (m.contains("BAC")) heSo = 100;
//            else if (m.contains("TRUNG")) heSo = 120;
//            else if (m.contains("NAM")) heSo = 120;
//        } else if (soXuyen == 5) {
//            if (m.contains("BAC")) heSo = 200;
//            else if (m.contains("TRUNG")) heSo = 250;
//            else if (m.contains("NAM")) heSo = 250;
//        }
//
//        return tien * heSo;
//    }
//
//    private double getHeSo2Chan(double tien, String mien) {
//        String m = removeDiacritics(mien).toUpperCase();
//        if (m.contains("BAC")) return tien * 70 / 27;
//        if (m.contains("TRUNG") || m.contains("NAM")) return tien * 70 / 18;
//        return 0;
//    }
//
//    private double getHeSoDau(double tien, String mien) {
//        String m = removeDiacritics(mien).toUpperCase();
//        if (m.contains("BAC")) return tien * 70 / 4;
//        if (m.contains("TRUNG") || m.contains("NAM")) return tien * 70;
//        return 0;
//    }
//
//    private double getHeSoDuoi(double tien, String mien) {
//        String m = removeDiacritics(mien).toUpperCase();
//        return m.contains("BAC") || m.contains("TRUNG") || m.contains("NAM") ? tien * 70 : 0;
//    }
//
//    private double getHeSoDauDuoiCaCap(double tien, String mien) {
//        String m = removeDiacritics(mien).toUpperCase();
//        if (m.contains("BAC")) return tien * 70 / 5;
//        if (m.contains("TRUNG") || m.contains("NAM")) return tien * 70 / 2;
//        return 0;
//    }
//
//    private double getHeSoLonNho(double tien, String mien) {
//        String m = removeDiacritics(mien).toUpperCase();
//        if (m.contains("BAC") || m.contains("TRUNG") || m.contains("NAM")) return tien * 1.95;
//        return 0;
//    }
//    public double tinhTongTien2Chan(String mien, double tienDanh, int soLanTrung) {
//        if (soLanTrung <= 0) return 0;
//        double tienMotLan = getHeSo2Chan(tienDanh, mien);
//        return tienMotLan * soLanTrung;
//    }
//
//
//    private String removeDiacritics(String input) {
//        String normalized = Normalizer.normalize(input, Normalizer.Form.NFD);
//        return Pattern.compile("\\p{InCombiningDiacriticalMarks}+")
//                .matcher(normalized)
//                .replaceAll("");
//    }
//}
package com.example.doxoso.service;

import com.example.doxoso.model.DoiChieuKetQuaDto;
import com.example.doxoso.repository.KetQuaMienBacRepository;
import com.example.doxoso.repository.KetQuaMienNamRepository;
import com.example.doxoso.repository.KetQuaMienTrungRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.Normalizer;
import java.util.List;
import java.util.regex.Pattern;

@Service
public class TinhTienService implements ITinhTienService {
    @Autowired
    KetQuaMienBacRepository bacRepo;
    @Autowired
    KetQuaMienNamRepository namRepo;
    @Autowired
    KetQuaMienTrungRepository trungRepo;

    public double tinhTienTrung(String cachDanh, String tienDanh, String mien) {
        String loai = removeDiacritics(cachDanh).toUpperCase().replaceAll("\\s+", "");
        double tienDanhDouble;

//        if (loai.equals("3CHAN")) return 0; // đã xử lý riêng

        try {
            tienDanhDouble = Double.parseDouble(tienDanh);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Giá trị tiền không hợp lệ: " + tienDanh, e);
        }

        return switch (loai) {
            case "XUYEN2" -> getHeSoXuyen(2, tienDanhDouble, mien);
            case "XUYEN3" -> getHeSoXuyen(3, tienDanhDouble, mien);
            case "XUYEN4" -> getHeSoXuyen(4, tienDanhDouble, mien);
            case "XUYEN5" -> getHeSoXuyen(5, tienDanhDouble, mien);
            case "2CHAN"   -> getHeSo2Chan(tienDanhDouble, mien);
            case "DAU"     -> getHeSoDau(tienDanhDouble, mien);
            case "DUOI"    -> getHeSoDuoi(tienDanhDouble, mien);
            case "DAUDUOI" -> getHeSoDauDuoiCaCap(tienDanhDouble, mien);
            case "LON", "NHO" -> getHeSoLonNho(tienDanhDouble, mien);
            default -> 0;
        };
    }

    //========== TÍNH TIỀN ĐẦU ĐUÔI ==================
    public double tinhTienDauDuoi(boolean trungDau, boolean trungDuoi,
                                         String mienTrungDau, String mienTrungDuoi,
                                         double tienDanhCuaDai) {
        double tien = 0;

        if (trungDau && trungDuoi) {
            double tienDau = tienDanhCuaDai / 2;
            double tienDuoi = tienDanhCuaDai / 2;

            tien += tinhTienDau(true, mienTrungDau, tienDau);
            tien += tinhTienDuoi(true, mienTrungDuoi, tienDuoi);

        } else if (trungDau) {
            tien = tinhTienDau(true, mienTrungDau, tienDanhCuaDai);

        } else if (trungDuoi) {
            tien = tinhTienDuoi(true, mienTrungDuoi, tienDanhCuaDai);

        } else {
            tien = 0;
        }

        return tien;
    }





    // === TÍNH TIỀN ĐẦU ===
    double tinhTienDau(boolean trungDau, String mien, double tienDanh) {
        if (!trungDau) return 0;
        switch (mien) {
            case "MIỀN BẮC":
                return tienDanh * 70/4;
            case "MIỀN TRUNG":
                return tienDanh * 70;
            case "MIỀN NAM":
                return tienDanh * 70;
            default:
                return 0;
        }
    }
    // === TÍNH TIỀN ĐUÔI ===
    double tinhTienDuoi(boolean trungDuoi, String mien, double tienDanh) {
        if (!trungDuoi) return 0;
        switch (mien) {
            case "MIỀN BẮC":
                return tienDanh * 70;
            case "MIỀN TRUNG":
                return tienDanh * 70;
            case "MIỀN NAM":
                return tienDanh * 70;
            default:
                return 0;
        }
    }

    // === TÍNH TIỀN 3 CHÂN ===
    public double[] tinhTien3Chan(String tienChuoi, String mien, List<String> giaiTrung) {
        double[] tien = tachTienDanhBaChan(tienChuoi);
        double tienBaoLo = tien[0];
        double tienThuong = tien[1];
        double tienDacBiet = tien[2];

        double tongTien = 0;
        double tongBaoLo = 0;
        double tongThuong = 0;
        double tongDacBiet = 0;

        for (String giai : giaiTrung) {
            String g = chuanHoaTenGiai(giai);

            if (mien.contains("BẮC")) {
                // ==================== MIỀN BẮC ====================
                if (g.equals("ĐẶC BIỆT")) {
                    double tienDB = tienDacBiet * 600;
                    double tienBL = tienBaoLo * 600 / 23;
                    double tienT = tienThuong * 600 / 10;

                    tongDacBiet += tienDB;
                    tongBaoLo += tienBL;
                    tongThuong += tienT;
                    tongTien += tienDB + tienBL + tienT;

                } else if (isLoThuong(mien, g)) {
                    double tienBL = tienBaoLo * 600 / 23;
                    double tienT = tienThuong * 600 / 10;

                    tongBaoLo += tienBL;
                    tongThuong += tienT;
                    tongTien += tienBL + tienT;

                } else {
                    double tienBL = tienBaoLo * 600 / 23;

                    tongBaoLo += tienBL;
                    tongTien += tienBL;
                }

            } else if (mien.contains("TRUNG")) {
                // ==================== MIỀN TRUNG ====================
                if (g.equals("ĐẶC BIỆT")) {
                    double tienDB = tienDacBiet * 600;
                    double tienBL = tienBaoLo * 600 / 17;
                    double tienT = tienThuong * 100;

                    tongDacBiet += tienDB;
                    tongBaoLo += tienBL;
                    tongThuong += tienT;
                    tongTien += tienDB + tienBL + tienT;

                } else if (isLoThuong(mien, g)) {
                    double tienBL = tienBaoLo * 600 / 17;
                    double tienT = tienThuong * 100;

                    tongBaoLo += tienBL;
                    tongThuong += tienT;
                    tongTien += tienBL + tienT;

                } else {
                    double tienBL = tienBaoLo * 600 / 17;

                    tongBaoLo += tienBL;
                    tongTien += tienBL;
                }

            } else if (mien.contains("NAM")) {
                // ==================== MIỀN NAM ====================
                if (g.equals("ĐẶC BIỆT")) {
                    double tienDB = tienDacBiet * 600;
                    double tienBL = tienBaoLo * 600 / 17;
                    double tienT = tienThuong * 100;

                    tongDacBiet += tienDB;
                    tongBaoLo += tienBL;
                    tongThuong += tienT;
                    tongTien += tienDB + tienBL + tienT;

                } else if (isLoThuong(mien, g)) {
                    double tienBL = tienBaoLo * 600 / 17;
                    double tienT = tienThuong * 100;

                    tongBaoLo += tienBL;
                    tongThuong += tienT;
                    tongTien += tienBL + tienT;

                } else {
                    double tienBL = tienBaoLo * 600 / 17;

                    tongBaoLo += tienBL;
                    tongTien += tienBL;
                }
            }
        }

        return new double[]{
                tongTien, tongBaoLo, tongThuong, tongDacBiet
        };
    }


    //    // Xác định có phải lô thượng hay không (tùy miền)
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
    private double[] tachTienDanhBaChan(String chuoi) {
        try {
            String[] parts = chuoi.split("-");
            double[] tien = new double[3];
            for (int i = 0; i < parts.length && i < 3; i++) {
                tien[i] = Double.parseDouble(parts[i]);
            }
            return tien;
        } catch (Exception e) {
            throw new IllegalArgumentException("Tiền đánh không hợp lệ cho 3 CHÂN: " + chuoi);
        }
    }





//=========== HÀM PHỤ =========
    private double getHeSoXuyen(int soXuyen, double tien, String mien) {
        String m = removeDiacritics(mien).toUpperCase();
        int heSo = switch (soXuyen) {
            case 2 -> m.contains("BAC") ? 10 : 15;
            case 3 -> m.contains("BAC") ? 40 : 60;
            case 4 -> m.contains("BAC") ? 100 : 120;
            case 5 -> m.contains("BAC") ? 200 : 250;
            default -> 0;
        };
        return tien * heSo;
    }

//===== TÍNH TIỀN 2 CHÂN =======
    public double tinhTongTien2Chan(String mien, double tienDanh, int soLanTrung) {
        if (soLanTrung <= 0) return 0;
        double tienMotLan = getHeSo2Chan(tienDanh, mien);
        return tienMotLan * soLanTrung;
    }

    private double getHeSo2Chan(double tien, String mien) {
        String m = removeDiacritics(mien).toUpperCase();
        if (m.contains("BAC")) return tien * 70 / 27;
        if (m.contains("TRUNG") || m.contains("NAM")) return tien * 70 / 18;
        return 0;
    }



    private double getHeSoDau(double tien, String mien) {
        String m = removeDiacritics(mien).toUpperCase();
        if (m.contains("BAC")) return tien * 70 / 4;
        if (m.contains("TRUNG") || m.contains("NAM")) return tien * 70;
        return 0;
    }

    private double getHeSoDuoi(double tien, String mien) {
        String m = removeDiacritics(mien).toUpperCase();
        return m.contains("BAC") || m.contains("TRUNG") || m.contains("NAM") ? tien * 70 : 0;
    }

    public double tinhTienDauDuoiCaCap(double tienDanh, String mien) {
        return tinhTienDau(true, mien, tienDanh) + tinhTienDuoi(true, mien, tienDanh);
    }


    private double getHeSoDauDuoiCaCap(double tien, String mien) {
        String m = removeDiacritics(mien).toUpperCase();
        if (m.contains("BAC")) return tien * 70 / 5;
        if (m.contains("TRUNG") || m.contains("NAM")) return tien * 70 / 2;
        return 0;
    }

    private double getHeSoLonNho(double tien, String mien) {
        String m = removeDiacritics(mien).toUpperCase();
        if (m.contains("BAC") || m.contains("TRUNG") || m.contains("NAM")) return tien * 1.95;
        return 0;
    }

    private String removeDiacritics(String input) {
        String normalized = Normalizer.normalize(input, Normalizer.Form.NFD);
        return Pattern.compile("\\p{InCombiningDiacriticalMarks}+")
                .matcher(normalized)
                .replaceAll("");
    }




    private boolean chuaDu3ChuSo(String soTrung, String soNguoiChoi) {
        if (soTrung == null || soTrung.length() < 3 || soNguoiChoi.length() != 3) return false;
        String baSoCuoi = soTrung.substring(soTrung.length() - 3);
        return baSoCuoi.equals(soNguoiChoi);
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
