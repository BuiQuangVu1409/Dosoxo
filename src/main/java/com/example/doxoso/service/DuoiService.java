
package com.example.doxoso.service;

import com.example.doxoso.model.DoiChieuKetQuaDto;
import com.example.doxoso.repository.KetQuaMienBacRepository;
import com.example.doxoso.repository.KetQuaMienNamRepository;
import com.example.doxoso.repository.KetQuaMienTrungRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.Normalizer;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

@Service
public class DuoiService {

    @Autowired
    private KetQuaMienBacRepository bacRepo;

    @Autowired
    private KetQuaMienTrungRepository trungRepo;

    @Autowired
    private KetQuaMienNamRepository namRepo;

    @Autowired
    private TinhTienService tinhTienService;

    public DoiChieuKetQuaDto xuLyDuoi(String soDanh, String mien, LocalDate ngay, String tienDanh, String tenDai) {
        DoiChieuKetQuaDto dto = new DoiChieuKetQuaDto();
        dto.setSoDanh(soDanh);
        dto.setTenDai(tenDai.trim());
        dto.setMien(mien);
        dto.setNgay(ngay);
        dto.setThu(chuyenNgaySangThu(ngay));
        dto.setCachDanh("ĐUÔI");
        dto.setCachTrung("ĐUÔI");
        dto.setTienDanh(tienDanh);

        // Validate cơ bản
        if (soDanh == null || tienDanh == null) {
            dto.setTrung(false);
            dto.setTienTrung(0.0);
            dto.setSaiLyDo(List.of("Thiếu thông tin bắt buộc (số đánh hoặc tiền đánh)"));
            return dto;
        }
        if (!soDanh.matches("\\d{2}")) {
            dto.setTrung(false);
            dto.setTienTrung(0.0);
            dto.setSaiLyDo(List.of("Số đánh không hợp lệ (phải là 2 chữ số)"));
            return dto;
        }
        if (tenDai == null || tenDai.isBlank()) {
            dto.setTrung(false);
            dto.setTienTrung(0.0);
            dto.setSaiLyDo(List.of("Bạn phải nhập tên đài muốn dò"));
            return dto;
        }

        List<String> dsDaiTrongNgay = layDanhSachDaiCoDuoiTrongNgay(mien, ngay);
        if (dsDaiTrongNgay.isEmpty()) {
            dto.setTrung(false);
            dto.setTienTrung(0.0);
            dto.setSaiLyDo(List.of("Không có đài mở thưởng trong ngày " + ngay));
            return dto;
        }

        // Xác định danh sách đài người chơi chọn
        List<String> dsDaiNguoiChoi;
        String tenDaiNormalized = tenDai.trim().toUpperCase();
        if (tenDaiNormalized.matches("\\d+\\s*ĐÀI")) {
            int soDaiNguoiNhap = Integer.parseInt(tenDaiNormalized.split("\\s+")[0]);
            if (soDaiNguoiNhap == dsDaiTrongNgay.size()) {
                dsDaiNguoiChoi = new ArrayList<>(dsDaiTrongNgay);
            } else {
                dto.setTrung(false);
                dto.setTienTrung(0.0);
                dto.setSaiLyDo(List.of("Số đài nhập (" + soDaiNguoiNhap + ") không khớp với số đài mở thưởng trong ngày (" + dsDaiTrongNgay.size() + ")"));
                dto.setDanhSachDai(dsDaiTrongNgay);
                return dto;
            }
        } else {
            dsDaiNguoiChoi = Arrays.stream(tenDai.split(","))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .collect(Collectors.toList());

            List<String> dsSai = dsDaiNguoiChoi.stream()
                    .filter(dai -> dsDaiTrongNgay.stream().noneMatch(d -> d.equalsIgnoreCase(dai)))
                    .collect(Collectors.toList());

            if (!dsSai.isEmpty()) {
                dto.setTrung(false);
                dto.setTienTrung(0.0);
                dto.setSaiLyDo(List.of("Tên đài không hợp lệ: " + String.join(", ", dsSai)));
                dto.setDanhSachDai(dsDaiTrongNgay);
                return dto;
            }
        }

        // Chuyển tiền đánh
        double tienDanhDouble;
        try {
            tienDanhDouble = Double.parseDouble(tienDanh);
        } catch (NumberFormatException e) {
            dto.setTrung(false);
            dto.setTienTrung(0.0);
            dto.setSaiLyDo(List.of("Tiền đánh không hợp lệ"));
            return dto;
        }

        double tienDanhMoiDai = tienDanhDouble / dsDaiNguoiChoi.size();
        AtomicBoolean daTrung = new AtomicBoolean(false);
        double[] tongTienTrung = {0.0};

        // Xử lý từng đài
        List<DoiChieuKetQuaDto.KetQuaTheoDai> ketQuaTungDaiList = dsDaiNguoiChoi.stream().map(daiNguoiChoi -> {
            DoiChieuKetQuaDto.KetQuaTheoDai ketQuaDai = new DoiChieuKetQuaDto.KetQuaTheoDai();
            ketQuaDai.setTenDai(daiNguoiChoi);
            ketQuaDai.setMien(mien);

            List<?> ketQuaDaiHienTai = getKetQuaTheoDaiVaNgay(mien, daiNguoiChoi, ngay);

            List<String> giaiTrung = ketQuaDaiHienTai.stream()
                    .filter(kq -> {
                        String soTrung = (String) getField(kq, "getSoTrung");
                        if (soTrung == null || soTrung.length() < 2) return false;
                        String duoi = soTrung.substring(soTrung.length() - 2);
                        return duoi.equals(soDanh);
                    })
                    .map(kq -> (String) getField(kq, "getGiai"))
                    .distinct()
                    .collect(Collectors.toList());

            if (!giaiTrung.isEmpty()) {
                ketQuaDai.setTrung(true);
                ketQuaDai.setGiaiTrung(giaiTrung);
                ketQuaDai.setSoTrung(soDanh);
                ketQuaDai.setSoLanTrung(giaiTrung.size());

                double tienTrungDai = tinhTienService.tinhTienDuoi(true, mien, tienDanhMoiDai) * ketQuaDai.getSoLanTrung();
                ketQuaDai.setTienTrung(tienTrungDai);

                tongTienTrung[0] += tienTrungDai;
                daTrung.set(true);
            } else {
                ketQuaDai.setTrung(false);
                ketQuaDai.setTienTrung(0.0);
                ketQuaDai.setLyDo("Không có số trúng " + soDanh);
                ketQuaDai.setSoLanTrung(0);
            }

            return ketQuaDai;
        }).collect(Collectors.toList());

        // Set dữ liệu cuối cùng
        dto.setTrung(daTrung.get());
        dto.setTienTrung(tongTienTrung[0]);
        dto.setDanhSachDai(dsDaiNguoiChoi);
        dto.setKetQuaTungDai(ketQuaTungDaiList);

        return dto;
    }

    private List<String> layDanhSachDaiCoDuoiTrongNgay(String mien, LocalDate ngay) {
        switch (mien.toUpperCase()) {
            case "MIỀN BẮC":
                return bacRepo.findAllByNgay(ngay).stream()
                        .map(kq -> kq.getTenDai().trim())
                        .distinct()
                        .toList();
            case "MIỀN TRUNG":
                return trungRepo.findAllByNgay(ngay).stream()
                        .map(kq -> kq.getTenDai().trim())
                        .distinct()
                        .toList();
            case "MIỀN NAM":
                return namRepo.findAllByNgay(ngay).stream()
                        .map(kq -> kq.getTenDai().trim())
                        .distinct()
                        .toList();
            default:
                return Collections.emptyList();
        }
    }

    private List<?> getKetQuaTheoDaiVaNgay(String mien, String tenDai, LocalDate ngay) {
        switch (mien.toUpperCase()) {
            case "MIỀN BẮC":
                return bacRepo.findAllByNgay(ngay).stream()
                        .filter(kq -> chuanHoa(kq.getTenDai()).equals(chuanHoa(tenDai)))
                        .toList();
            case "MIỀN TRUNG":
                return trungRepo.findAllByNgay(ngay).stream()
                        .filter(kq -> chuanHoa(kq.getTenDai()).equals(chuanHoa(tenDai)))
                        .toList();
            case "MIỀN NAM":
                return namRepo.findAllByNgay(ngay).stream()
                        .filter(kq -> chuanHoa(kq.getTenDai()).equals(chuanHoa(tenDai)))
                        .toList();
            default:
                return Collections.emptyList();
        }
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
                .replaceAll("đ", "d")
                .replaceAll("Đ", "D")
                .replaceAll("\\p{M}", "")
                .toUpperCase()
                .trim()
                .replaceAll("\\s+", "");
    }

    private <T> T getField(Object obj, String methodName) {
        try {
            return (T) obj.getClass().getMethod(methodName).invoke(obj);
        } catch (Exception e) {
            throw new RuntimeException("Lỗi phản xạ khi lấy field: " + methodName, e);
        }
    }
}
