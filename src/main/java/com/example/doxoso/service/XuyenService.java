package com.example.doxoso.service;

import com.example.doxoso.repository.KetQuaMienBacRepository;
import com.example.doxoso.repository.KetQuaMienNamRepository;
import com.example.doxoso.repository.KetQuaMienTrungRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.Normalizer;
import java.time.LocalDate;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
//
//@Service
//public class XuyenService {
//
//    @Autowired
//    private SoCuoiService soCuoiService;
//
//    // Nhận diện có phải cách đánh XUYÊN không
//    public boolean laCachDanhXuyen(String cachDanh) {
//        if (cachDanh == null) return false;
//        return cachDanh.toUpperCase().replace(" ", "").startsWith("XUYEN");
//    }
//
//    // Tách số: "02-34-12" → ["02", "34", "12"]
//    public List<String> tachSoDaDanh(String soDanh) {
//        return Arrays.stream(soDanh.split("[-,]"))
//                .map(s -> {
//                    String so = s.trim().replaceFirst("^0+", "");
//                    return so.length() == 1 ? "0" + so : so;
//                })
//                .collect(Collectors.toList());
//    }
//
//    // Kiểm tra số trúng xuyên
//    public Optional<String> xuLyTrungXuyen(String cachDanh, String soDanh, LocalDate ngay, String mien) {
//        if (!laCachDanhXuyen(cachDanh)) return Optional.empty();
//
//        List<String> soNguoiDanh = tachSoDaDanh(soDanh);
//        Map<String, List<String>> mapSoTrung = soCuoiService.lay2SoCuoiCuaTatCaDai(mien, ngay);
//
//        for (Map.Entry<String, List<String>> entry : mapSoTrung.entrySet()) {
//            String tenDai = entry.getKey();
//            List<String> soTrungTrongDai = entry.getValue();
//
//            if (laTrungXuyen(soNguoiDanh, soTrungTrongDai)) {
//                return Optional.of(tenDai); // ✅ Trúng xuyên tại đài này
//            }
//        }
//
//        return Optional.empty(); // ❌ Không trúng đài nào
//    }
//
//    // Tất cả số người đánh đều nằm trong list số trúng của đài
//    public boolean laTrungXuyen(List<String> soNguoiDanh, List<String> soTrungTrongDai) {
//        return soNguoiDanh.stream().allMatch(soTrungTrongDai::contains);
//    }
//}
@Service
public class XuyenService {

    @Autowired
    private KetQuaMienBacRepository bacRepo;

    @Autowired
    private KetQuaMienTrungRepository trungRepo;

    @Autowired
    private KetQuaMienNamRepository namRepo;

    @Autowired
    private ChuyenDoiNgayService chuyenDoiNgayService;

    @Autowired
    private DanhSachDaiTheoMienService danhSachDaiTheoMienService;

    // Nhận biết cách đánh có phải xuyên không
    public boolean laCachDanhXuyen(String cachDanh) {
        if (cachDanh == null) return false;
        String cd = removeDiacritics(cachDanh).toUpperCase().replaceAll("\\s+", "");
        return cd.startsWith("XUYEN");
    }


    // Xử lý trúng xuyên, trả về Optional tên đài nếu trúng
    public Optional<String> xuLyTrungXuyen(String cachDanh, String soDanh, LocalDate ngay, String mien) {
        List<String> cacSo = List.of(soDanh.split("-")); // Ví dụ: "29-73" -> ["29", "73"]
        List<String> danhSachDai = layDanhSachDaiTheoNgayVaMien(ngay, mien);

        List<?> danhSachKetQua = switch (mien.toUpperCase()) {
            case "MIỀN BẮC" -> bacRepo.findAllByNgay(ngay);
            case "MIỀN TRUNG" -> trungRepo.findAllByNgay(ngay);
            case "MIỀN NAM" -> namRepo.findAllByNgay(ngay);
            default -> List.of();
        };

        // Gom kết quả theo từng đài
        Map<String, List<String>> mapKetQuaTheoDai = danhSachKetQua.stream()
                .filter(kq -> {
                    String tenDai = getField(kq, "getTenDai");
                    return tenDai != null && danhSachDai.contains(tenDai.toUpperCase());
                })
                .collect(Collectors.groupingBy(
                        kq -> {
                            String tenDai = getField(kq, "getTenDai");
                            return tenDai != null ? tenDai.toUpperCase() : "UNKNOWN";
                        },
                        Collectors.mapping(
                                kq -> {
                                    String soTrung = getField(kq, "getSoTrung");
                                    if (soTrung != null && soTrung.length() >= 2) {
                                        return soTrung.substring(soTrung.length() - 2);
                                    }
                                    return "";
                                },
                                Collectors.toList()
                        )
                ));


        // Kiểm tra xem có đài nào chứa đủ tất cả số không
        for (String tenDai : mapKetQuaTheoDai.keySet()) {
            List<String> soTrungCuoi = mapKetQuaTheoDai.get(tenDai);
            if (cacSo.stream().allMatch(soTrungCuoi::contains)) {
                return Optional.of(tenDai);
            }
        }
        return Optional.empty();
    }

    private List<String> layDanhSachDaiTheoNgayVaMien(LocalDate ngay, String mien) {
        String thu = chuyenDoiNgayService.chuyenDoiThu(ngay);
        return danhSachDaiTheoMienService.layDanhSachDaiTheoThuVaMien(thu, mien);
    }

    private <T> T getField(Object obj, String methodName) {
        try {
            return (T) obj.getClass().getMethod(methodName).invoke(obj);
        } catch (Exception e) {
            throw new RuntimeException("Lỗi phản xạ khi lấy field: " + methodName);
        }
    }
    private String removeDiacritics(String input) {
        String normalized = Normalizer.normalize(input, Normalizer.Form.NFD);
        return Pattern.compile("\\p{InCombiningDiacriticalMarks}+")
                .matcher(normalized)
                .replaceAll("");
    }
}
