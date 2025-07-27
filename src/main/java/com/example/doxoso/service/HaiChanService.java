package com.example.doxoso.service;

import com.example.doxoso.model.SoNguoiChoi;
import com.example.doxoso.repository.KetQuaMienBacRepository;
import com.example.doxoso.repository.KetQuaMienNamRepository;
import com.example.doxoso.repository.KetQuaMienTrungRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class HaiChanService {

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

    public boolean kiemTraTrung2Chan(SoNguoiChoi so) {
        List<String> danhSach2SoCuoi = lay2SoCuoiTrongNgayVaDai(so);
        return danhSach2SoCuoi.contains(so.getSoDanh());
    }
        // dò nhiều đài trong 1 miền
        public List<String> xacDinhDanhSachDaiCanDo(SoNguoiChoi so) {
            String thu = chuyenDoiNgayService.chuyenDoiThu(so.getNgay());
            String mien = so.getMien();
            LocalDate ngay = so.getNgay();

            List<String> tatCaDai = danhSachDaiTheoMienService.layDanhSachDaiTheoThuVaMien(thu, mien)
                    .stream()
                    .filter(Objects::nonNull)                      // bỏ đài null
                    .map(String::trim)
                    .filter(d -> !d.isEmpty())                     // bỏ đài rỗng hoặc toàn khoảng trắng
                    .map(String::toUpperCase)
                    .distinct()
                    .collect(Collectors.toList());


            if (so.getTenDai() != null && !so.getTenDai().isBlank()) {
                String tenDaiChuanHoa = so.getTenDai().trim().toUpperCase();

                // Trường hợp người chơi ghi "2 ĐÀI", "3 ĐÀI"
                if (tenDaiChuanHoa.matches("\\d+ ĐÀI")) {
                    int soDaiMuonDo = Integer.parseInt(tenDaiChuanHoa.split(" ")[0]);

                    if (soDaiMuonDo < 2) {
                        throw new IllegalArgumentException("Bạn phải chọn ít nhất 2 đài để dò 2 CHÂN.");
                    }

                    int soDaiHienTai = tatCaDai.size();

                    if (soDaiHienTai < soDaiMuonDo) {
                        System.out.println("⚠️ Cảnh báo: Người chơi muốn dò " + soDaiMuonDo + " đài, nhưng chỉ có " + soDaiHienTai + " đài trong ngày.");
                        return tatCaDai; // Trả về tất cả các đài hiện có thay vì lỗi
                    }



                    if (soDaiHienTai >= soDaiMuonDo) {
                        return tatCaDai.subList(0, soDaiMuonDo);
                    }


                    return tatCaDai.subList(0, soDaiMuonDo);
                }

                // Trường hợp người chơi nhập tên đài cụ thể (ví dụ: HCM, LONG AN)
                return Arrays.stream(tenDaiChuanHoa.split(","))
                        .map(String::trim)
                        .filter(d -> !d.isEmpty())
                        .collect(Collectors.toList());
            }

            // Không nhập gì → dò toàn bộ đài trong ngày
            return tatCaDai;
        }



    public List<String> lay2SoCuoiTrongNgayVaDai(SoNguoiChoi so) {
        List<String> danhSachDai = xacDinhDanhSachDaiCanDo(so);
        List<?> danhSachKetQua = switch (so.getMien().toUpperCase()) {
            case "MIỀN BẮC" -> bacRepo.findAllByNgay(so.getNgay());
            case "MIỀN TRUNG" -> trungRepo.findAllByNgay(so.getNgay());
            case "MIỀN NAM" -> namRepo.findAllByNgay(so.getNgay());
            default -> List.of();
        };

        return danhSachKetQua.stream()
                .filter(kq -> {
                    String tenDai = (String) getField(kq, "getTenDai");
                    return tenDai != null && danhSachDai.contains(tenDai.toUpperCase());
                })
                .map(kq -> (String) getField(kq, "getSoTrung"))
                .filter(s -> s != null && s.length() >= 2)
                .map(s -> s.substring(s.length() - 2))
                .collect(Collectors.toList());
    }
    private String chuanHoaMien(String mien) {
        return switch (mien.toUpperCase()) {
            case "MIENBAC" -> "MIỀN BẮC";
            case "MIENTRUNG" -> "MIỀN TRUNG";
            case "MIENNAM" -> "MIỀN NAM";
            default -> mien.toUpperCase();
        };
    }

    private <T> T getField(Object obj, String methodName) {
        try {
            return (T) obj.getClass().getMethod(methodName).invoke(obj);
        } catch (Exception e) {
            throw new RuntimeException("Lỗi phản xạ khi lấy field: " + methodName);
        }
    }
}