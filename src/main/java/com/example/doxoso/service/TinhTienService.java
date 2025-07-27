package com.example.doxoso.service;

import com.example.doxoso.model.KetQuaMienBac;
import com.example.doxoso.repository.KetQuaMienBacRepository;
import com.example.doxoso.repository.KetQuaMienNamRepository;
import com.example.doxoso.repository.KetQuaMienTrungRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.Method;
import java.text.Normalizer;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
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

        if (loai.equals("3CHAN")) return 0; // đã xử lý riêng

        double tienDanhDouble;
        try {
            tienDanhDouble = Double.parseDouble(tienDanh);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Giá trị tiền không hợp lệ: " + tienDanh, e);
        }

        return switch (loai) {
            //tính tiền xuyên
            case "XUYEN2" -> {
                String m = removeDiacritics(mien).toUpperCase();
                if (m.contains("BAC")) {
                    yield tienDanhDouble * 10;
                } else if (m.contains("TRUNG")) {
                    yield tienDanhDouble * 15;
                } else if (m.contains("NAM")) {
                    yield tienDanhDouble * 15;
                } else {
                    yield 0;
                }
            }
            case "XUYEN3" -> {
                String m = removeDiacritics(mien).toUpperCase();
                if (m.contains("BAC")) {
                    yield tienDanhDouble * 40;
                } else if (m.contains("TRUNG")) {
                    yield tienDanhDouble * 60;
                } else if (m.contains("NAM")) {
                    yield tienDanhDouble * 60;
                } else {
                    yield 0;
                }
            }
            case "XUYEN4" -> {
                String m = removeDiacritics(mien).toUpperCase();
                if (m.contains("BAC")) {
                    yield tienDanhDouble * 100;
                } else if (m.contains("TRUNG")) {
                    yield tienDanhDouble * 120;
                } else if (m.contains("NAM")) {
                    yield tienDanhDouble * 120;
                } else {
                    yield 0;
                }
            }
            case "XUYEN5" -> {
                String m = removeDiacritics(mien).toUpperCase();
                if (m.contains("BAC")) {
                    yield tienDanhDouble * 200;
                } else if (m.contains("TRUNG")) {
                    yield tienDanhDouble * 250;
                } else if (m.contains("NAM")) {
                    yield tienDanhDouble * 250;
                } else {
                    yield 0;
                }
            }


            // tính tiền 2 chân
            case "2CHAN" -> {
                String m = removeDiacritics(mien).toUpperCase().trim();
                if (m.contains("BAC")) {
                    yield tienDanhDouble * 70 / 27 ;
                } else if (m.contains("TRUNG")) {
                    yield tienDanhDouble * 70 / 18;
                } else if (m.contains("NAM")) {
                    yield tienDanhDouble * 70/ 18 ;
                } else {
                    yield 0;
                }
            }

//            case "DAUMIENBAC" -> tienDanhDouble * 70 / 4 ;
//            case "DAUMIENTRUNG", "DAUMIENNAM" -> tienDanhDouble * 70;
//            case "DUOIMIENBAC" -> tienDanhDouble * 70;
//            case "DUOIMIENTRUNG", "DUOIMIENNAM" -> tienDanhDouble * 70;
//            case "DAUDUOIMIENBAC" -> tienDanhDouble * 70 / 5;
//            case "DAUDUOIMIENTRUNG", "DAUDUOIMIENNAM" -> tienDanhDouble * 70 / 2 ;

            case "DAU" -> {
                String m = removeDiacritics(mien).toUpperCase();
                if (m.contains("BAC")) {
                    yield tienDanhDouble * 70 / 4;
                } else if (m.contains("TRUNG")) {
                    yield tienDanhDouble * 70;
                } else if (m.contains("NAM")) {
                    yield tienDanhDouble * 70;
                } else {
                    yield 0;
                }
            }
            // ĐUÔI
            case "DUOI" -> {
                String m = removeDiacritics(mien).toUpperCase();
                if (m.contains("BAC")) {
                    yield tienDanhDouble * 70;
                } else if (m.contains("TRUNG")) {
                    yield tienDanhDouble * 70;
                } else if (m.contains("NAM")) {
                    yield tienDanhDouble * 70;
                } else {
                    yield 0;
                }
            }

            // ĐẦU ĐUÔI
            case "DAUDUOI" -> {
                String m = removeDiacritics(mien).toUpperCase();
                if (m.contains("BAC")) {
                    yield tienDanhDouble * 70 / 5;
                } else if (m.contains("TRUNG")) {
                    yield tienDanhDouble * 70 / 2;
                } else if (m.contains("NAM")) {
                    yield tienDanhDouble * 70 / 2;
                } else {
                    yield 0;
                }
            }
            // LỚN , nhỏ
            case "LON", "NHO" -> {
                String m = removeDiacritics(mien).toUpperCase();
                if (m.contains("BAC")) {
                    yield tienDanhDouble * 1.95;
                } else if (m.contains("TRUNG") || m.contains("NAM")) {
                    yield tienDanhDouble * 1.95;
                } else {
                    yield 0;
                }
            }



            default -> 0;
        };
    }



    private String removeDiacritics(String input) {
        String normalized = Normalizer.normalize(input, Normalizer.Form.NFD);
        return Pattern.compile("\\p{InCombiningDiacriticalMarks}+")
                .matcher(normalized)
                .replaceAll("");
    }

}
//    private double tinhTienLon(double tien, String mien) {
//        String m = removeDiacritics(mien).toUpperCase();
//        if (m.contains("BAC")) return tien * 1.8;
//        if (m.contains("TRUNG") || m.contains("NAM")) return tien * 2;
//        return 0;
//    }
//
//    private double tinhTienNho(double tien, String mien) {
//        return tinhTienLon(tien, mien); // vì logic giống nhau
//    }
//case "LON" -> tinhTienLon(tienDanhDouble, mien);
//        case "NHO" -> tinhTienNho(tienDanhDouble, mien);
