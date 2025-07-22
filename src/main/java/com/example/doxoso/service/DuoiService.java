package com.example.doxoso.service;

import com.example.doxoso.model.DoiChieuKetQuaDto;

import com.example.doxoso.model.KetQuaMienBac;
import com.example.doxoso.repository.KetQuaMienBacRepository;
import com.example.doxoso.repository.KetQuaMienTrungRepository;
import com.example.doxoso.repository.KetQuaMienNamRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.Normalizer;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class DuoiService {

    @Autowired
    private KetQuaMienBacRepository bacRepo;

    @Autowired
    private KetQuaMienTrungRepository trungRepo;

    @Autowired
    private KetQuaMienNamRepository namRepo;

    public DoiChieuKetQuaDto xuLyDuoi(String soDanh, String mien, LocalDate ngay, String tienDanh) {
        DoiChieuKetQuaDto dto = new DoiChieuKetQuaDto();
        dto.setSoDanh(soDanh);
        dto.setMien(mien);
        dto.setNgay(ngay);
        dto.setThu(chuyenNgaySangThu(ngay));
        dto.setCachDanh("DUOI");
        dto.setTienDanh(tienDanh);
        dto.setDanhSachDai(new ArrayList<>());

        if (tienDanh == null || tienDanh.isBlank()) {
            dto.setTrung(false);
            dto.setTienTrung(0);
            dto.setSaiLyDo(List.of("Thiếu tiền đánh"));
            return dto;
        }

        if (!soDanh.matches("\\d{2}")) {
            dto.setTrung(false);
            dto.setTienTrung(0);
            dto.setSaiLyDo(List.of("Số đánh không hợp lệ (phải là 2 chữ số)"));
            return dto;
        }

        String soChuan = chuanHoa(soDanh);
        String mienChuan = chuanHoa(mien);
        List<String> saiLyDo = new ArrayList<>();

        if (mienChuan.equals("MIENBAC")) {
            List<KetQuaMienBac> ketQua = bacRepo.findAllByNgay(ngay);

            Set<String> cacDai = ketQua.stream()
                    .map(kq -> chuanHoa(kq.getTenDai()))
                    .collect(Collectors.toSet());

            if (cacDai.size() != 1) {
                dto.setTrung(false);
                dto.setTienTrung(0);
                dto.setSaiLyDo(List.of("Lỗi dữ liệu: MIỀN BẮC có nhiều hơn 1 đài trong ngày"));
                return dto;
            }

            String tenDai = cacDai.iterator().next();
            dto.setTenDai(tenDai);
            dto.setDanhSachDai(List.of(tenDai));

            Optional<KetQuaMienBac> giaiDb = ketQua.stream()
                    .filter(kq -> "ĐẶC BIỆT".equalsIgnoreCase(kq.getGiai()))
                    .findFirst();

            if (giaiDb.isEmpty()) {
                dto.setTrung(false);
                dto.setTienTrung(0);
                dto.setSaiLyDo(List.of("Không tìm thấy giải ĐB đài " + tenDai));
                return dto;
            }

            String soTrung = giaiDb.get().getSoTrung();
            String duoiKetQua = soTrung.substring(soTrung.length() - 2);

            if (soChuan.equals(duoiKetQua)) {
                dto.setTrung(true);
                dto.setGiaiTrung("Giải ĐB");
                dto.setCachTrung("Trúng ĐUÔI MIỀN BẮC – Đài " + tenDai);
                dto.setTienTrung(Double.parseDouble(tienDanh) * 70); // 1 ăn 70
            } else {
                dto.setTrung(false);
                dto.setTienTrung(0);
                dto.setSaiLyDo(List.of("Trật – đuôi GĐB là " + duoiKetQua));
            }

            return dto;
        }

        if (mienChuan.equals("MIENTRUNG")) {
            var ketQua = trungRepo.findAllByNgay(ngay).stream()
                    .filter(kq -> "ĐẶC BIỆT".equalsIgnoreCase(kq.getGiai()))
                    .toList();

            for (var kq : ketQua) {
                dto.getDanhSachDai().add(kq.getTenDai());
                String duoi = kq.getSoTrung().substring(kq.getSoTrung().length() - 2);
                if (soChuan.equals(duoi)) {
                    dto.setTrung(true);
                    dto.setGiaiTrung("Giải ĐB");
                    dto.setTenDai(kq.getTenDai());
                    dto.setCachTrung("Trúng ĐUÔI MIỀN TRUNG – Đài " + kq.getTenDai());
                    dto.setTienTrung(Double.parseDouble(tienDanh) * 70);
                    return dto;
                } else {
                    saiLyDo.add("Trật đài " + kq.getTenDai());

                }
            }
        }

        if (mienChuan.equals("MIENNAM")) {
            var ketQua = namRepo.findAllByNgay(ngay).stream()
                    .filter(kq -> "ĐẶC BIỆT".equalsIgnoreCase(kq.getGiai()))
                    .toList();

            for (var kq : ketQua) {
                dto.getDanhSachDai().add(kq.getTenDai());
                String duoi = kq.getSoTrung().substring(kq.getSoTrung().length() - 2);
                if (soChuan.equals(duoi)) {
                    dto.setTrung(true);
                    dto.setGiaiTrung("Giải ĐB");
                    dto.setTenDai(kq.getTenDai());
                    dto.setCachTrung("Trúng ĐUÔI MIỀN NAM – Đài " + kq.getTenDai());
                    dto.setTienTrung(Double.parseDouble(tienDanh) * 70);
                    return dto;
                } else {
                    saiLyDo.add("Trật đài " + kq.getTenDai());
                }
            }
        }

        dto.setTrung(false);
        dto.setTienTrung(0);
        dto.setSaiLyDo(saiLyDo.isEmpty() ? List.of("Không trúng số nào") : saiLyDo);
        return dto;
    }

    public boolean laCachDanh(String cachDanhChuanHoa) {
        return cachDanhChuanHoa != null && cachDanhChuanHoa.contains("DUOI");
    }

    private String chuyenNgaySangThu(LocalDate ngay) {
        DayOfWeek day = ngay.getDayOfWeek();
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

    private String chuanHoa(String input) {
        if (input == null) return "";
        return Normalizer.normalize(input, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .replaceAll("đ", "d")
                .replaceAll("Đ", "D")
                .toUpperCase()
                .trim()
                .replaceAll("\\s+", "");
    }
}
