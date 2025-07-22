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
    public double tinhTienTrung(String cachDanh, String tienDanh) {
        String loai = removeDiacritics(cachDanh).toUpperCase().replaceAll("\\s+", "");

        if (loai.equals("3CHAN")) return 0; // đã xử lý riêng

        double tienDanhDouble;
        try {
            tienDanhDouble = Double.parseDouble(tienDanh);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Giá trị tiền không hợp lệ: " + tienDanh, e);
        }

        return switch (loai) {
            case "XUYEN2" -> tienDanhDouble * 2000;
            case "XUYEN3" -> tienDanhDouble * 5000;
            case "XUYEN4" -> tienDanhDouble * 10000;
            case "2CHAN" -> tienDanhDouble * 1500;
//            case "DAU", "DUOI" -> tienDanhDouble * 700;
            case "BAOLO" -> tienDanhDouble * 90;
            case "DAUMIENBAC" -> tienDanhDouble * 800;
            case "DAUMIENTRUNG", "DAUMIENNAM" -> tienDanhDouble * 1500;
            case "DUOIMIENBAC" -> tienDanhDouble * 1200;
            case "DUOIMIENTRUNG", "DUOIMIENNAM" -> tienDanhDouble * 1500;
            case "DAUDUOIMIENBAC" -> tienDanhDouble * 1500;
            case "DAUDUOIMIENTRUNG", "DAUDUOIMIENNAM" -> tienDanhDouble * 2500;
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