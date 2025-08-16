package com.example.doxoso.service;

import com.example.doxoso.model.DoiChieuKetQuaDto;
import com.example.doxoso.model.SoNguoiChoi;
import com.example.doxoso.repository.KetQuaMienBacRepository;
import com.example.doxoso.repository.KetQuaMienNamRepository;
import com.example.doxoso.repository.KetQuaMienTrungRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
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


        // dò nhiều đài trong 1 miền
//        public List<String> xacDinhDanhSachDaiCanDo(SoNguoiChoi so) {
//            String thu = chuyenDoiNgayService.chuyenDoiThu(so.getNgay());
//            String mien = so.getMien();
//            LocalDate ngay = so.getNgay();
//
//            List<String> tatCaDai = danhSachDaiTheoMienService.layDanhSachDaiTheoThuVaMien(thu, mien)
//                    .stream()
//                    .filter(Objects::nonNull)                      // bỏ đài null
//                    .map(String::trim)
//                    .filter(d -> !d.isEmpty())                     // bỏ đài rỗng hoặc toàn khoảng trắng
//                    .map(String::toUpperCase)
//                    .distinct()
//                    .collect(Collectors.toList());
//
//
//            if (so.getTenDai() != null && !so.getTenDai().isBlank()) {
//                String tenDaiChuanHoa = so.getTenDai().trim().toUpperCase();
//
//                // Trường hợp người chơi ghi "2 ĐÀI", "3 ĐÀI"
//                if (tenDaiChuanHoa.matches("\\d+ ĐÀI")) {
//                    int soDaiMuonDo = Integer.parseInt(tenDaiChuanHoa.split(" ")[0]);
//
//                    if (soDaiMuonDo < 2) {
//                        throw new IllegalArgumentException("Bạn phải chọn ít nhất 2 đài để dò 2 CHÂN.");
//                    }
//
//                    int soDaiHienTai = tatCaDai.size();
//
//                    if (soDaiHienTai < soDaiMuonDo) {
//                        System.out.println("⚠️ Cảnh báo: Người chơi muốn dò " + soDaiMuonDo + " đài, nhưng chỉ có " + soDaiHienTai + " đài trong ngày.");
//                        return tatCaDai; // Trả về tất cả các đài hiện có thay vì lỗi
//                    }
//
//
//
//                    if (soDaiHienTai >= soDaiMuonDo) {
//                        return tatCaDai.subList(0, soDaiMuonDo);
//                    }
//
//
//                    return tatCaDai.subList(0, soDaiMuonDo);
//                }
//
//                // Trường hợp người chơi nhập tên đài cụ thể (ví dụ: HCM, LONG AN)
//                return Arrays.stream(tenDaiChuanHoa.split(","))
//                        .map(String::trim)
//                        .filter(d -> !d.isEmpty())
//                        .collect(Collectors.toList());
//            }
//
//            // Không nhập gì → dò toàn bộ đài trong ngày
//            return tatCaDai;
//        }
        public List<String> xacDinhDanhSachDaiCanDo(SoNguoiChoi so) {
            String thu = chuyenDoiNgayService.chuyenDoiThu(so.getNgay());
            String mien = so.getMien();
            LocalDate ngay = so.getNgay();

            // Lấy danh sách đài theo cấu hình (có thể không theo thứ tự từ kết quả thật)
            List<String> tatCaDai = danhSachDaiTheoMienService.layDanhSachDaiTheoThuVaMien(thu, mien)
                    .stream()
                    .filter(Objects::nonNull)
                    .map(String::trim)
                    .filter(d -> !d.isEmpty())
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

                    // 🔁 Lấy danh sách đài thực tế từ bảng kết quả (đúng thứ tự như trong DB)
                    List<?> danhSachKetQua = switch (mien.toUpperCase()) {
                        case "MIỀN BẮC" -> bacRepo.findAllByNgay(ngay);
                        case "MIỀN TRUNG" -> trungRepo.findAllByNgay(ngay);
                        case "MIỀN NAM" -> namRepo.findAllByNgay(ngay);
                        default -> List.of();
                    };

                    List<String> danhSachDaiThucTe = danhSachKetQua.stream()
                            .map(kq -> (String) getField(kq, "getTenDai"))
                            .filter(Objects::nonNull)
                            .map(String::trim)
                            .map(String::toUpperCase)
                            .distinct()
                            .collect(Collectors.toList());

                    int soDaiThucTe = danhSachDaiThucTe.size();

                    if (soDaiThucTe < soDaiMuonDo) {
                        System.out.println("⚠️ Người chơi muốn dò " + soDaiMuonDo + " đài, nhưng chỉ có " + soDaiThucTe + " đài thực tế.");
                        return danhSachDaiThucTe; // Trả về tất cả đài thực tế đang mở
                    }

                    return danhSachDaiThucTe.subList(0, soDaiMuonDo); // ✅ Trả về đúng số đài thực tế theo thứ tự kết quả
                }

                // Trường hợp người chơi nhập tên đài cụ thể (VD: "KHÁNH HOÀ, ĐÀ NẴNG")
                return Arrays.stream(tenDaiChuanHoa.split(","))
                        .map(String::trim)
                        .filter(d -> !d.isEmpty())
                        .map(String::toUpperCase)
                        .collect(Collectors.toList());
            }

            // Không nhập gì → dò toàn bộ đài trong ngày (theo config)
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
//    public DoiChieuKetQuaDto traVeKetQuaChiTiet2Chan(SoNguoiChoi so) {
//        DoiChieuKetQuaDto dto = new DoiChieuKetQuaDto();
//        dto.setSoDanh(so.getSoDanh());
//        dto.setCachTrung("2 chân");
//
//        List<String> danhSachDai = xacDinhDanhSachDaiCanDo(so);
//        List<?> ketQua = switch (so.getMien().toUpperCase()) {
//            case "MIỀN BẮC" -> bacRepo.findAllByNgay(so.getNgay());
//            case "MIỀN TRUNG" -> trungRepo.findAllByNgay(so.getNgay());
//            case "MIỀN NAM" -> namRepo.findAllByNgay(so.getNgay());
//            default -> List.of();
//        };
//
//        AtomicBoolean daTrung = new AtomicBoolean(false);
//
//
//        List<DoiChieuKetQuaDto.KetQuaTheoDai> danhSachKetQua = danhSachDai.stream().map(tenDai -> {
//            DoiChieuKetQuaDto.KetQuaTheoDai ketQuaDai = new DoiChieuKetQuaDto.KetQuaTheoDai();
//            ketQuaDai.setTenDai(tenDai);
//            ketQuaDai.setMien(xacDinhMienCuaDai(tenDai, so.getNgay()));
//            List<String> giaiTrung = ketQua.stream()
//                    .filter(kq -> tenDai.equalsIgnoreCase((String) getField(kq, "getTenDai")))
//                    .filter(kq -> {
//                        String soTrung = (String) getField(kq, "getSoTrung");
//                        if (soTrung != null && soTrung.length() >= 2) {
//                            String haiSoCuoi = soTrung.substring(soTrung.length() - 2);
//                            return haiSoCuoi.equals(so.getSoDanh());
//                        }
//                        return false;
//                    })
//                    .map(kq -> (String) getField(kq, "getGiai")) // phải có getGiai trong entity
//                    .collect(Collectors.toList());
//
//            if (!giaiTrung.isEmpty()) {
//                ketQuaDai.setTrung(true);
//                ketQuaDai.setSoTrung(so.getSoDanh());
//                ketQuaDai.setGiai(giaiTrung);
//                ketQuaDai.setSoLanTrung(giaiTrung.size());
//                daTrung.set(true);
//
//            } else {
//                ketQuaDai.setTrung(false);
//                ketQuaDai.setLyDo("Không có số trúng " + so.getSoDanh());
//            }
//
//            return ketQuaDai;
//        }).collect(Collectors.toList());
//
//        dto.setTrung(daTrung.get());
//
//        dto.setKetQuaTungDai(danhSachKetQua); // Gán đúng trường chi tiết
//
//        dto.setDanhSachDai(danhSachKetQua.stream()
//                .map(DoiChieuKetQuaDto.KetQuaTheoDai::getTenDai)
//                .collect(Collectors.toList())); // Gán danh sách tên đài riêng nếu cần
//        return dto;
//    }
        public DoiChieuKetQuaDto traVeKetQuaChiTiet2Chan(SoNguoiChoi so) {
            DoiChieuKetQuaDto dto = new DoiChieuKetQuaDto();
            dto.setSoDanh(so.getSoDanh());
            dto.setCachTrung("2 chân");

            List<String> danhSachDai = xacDinhDanhSachDaiCanDo(so); // chứa đài từ nhiều miền

            // Gộp hết tất cả kết quả từ 3 miền để xử lý chung
            List<Object> ketQuaTongHop = new ArrayList<>();
            ketQuaTongHop.addAll(bacRepo.findAllByNgay(so.getNgay()));
            ketQuaTongHop.addAll(trungRepo.findAllByNgay(so.getNgay()));
            ketQuaTongHop.addAll(namRepo.findAllByNgay(so.getNgay()));

            AtomicBoolean daTrung = new AtomicBoolean(false);

            List<DoiChieuKetQuaDto.KetQuaTheoDai> danhSachKetQua = danhSachDai.stream().map(tenDai -> {
                DoiChieuKetQuaDto.KetQuaTheoDai ketQuaDai = new DoiChieuKetQuaDto.KetQuaTheoDai();
                ketQuaDai.setTenDai(tenDai);
                ketQuaDai.setMien(xacDinhMienCuaDai(tenDai, so.getNgay()));

                List<String> giaiTrung = ketQuaTongHop.stream()
                        .filter(kq -> tenDai.equalsIgnoreCase((String) getField(kq, "getTenDai")))
                        .filter(kq -> {
                            String soTrung = (String) getField(kq, "getSoTrung");
                            if (soTrung != null && soTrung.length() >= 2) {
                                return soTrung.substring(soTrung.length() - 2).equals(so.getSoDanh());
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
            dto.setDanhSachDai(danhSachKetQua.stream().map(DoiChieuKetQuaDto.KetQuaTheoDai::getTenDai).collect(Collectors.toList()));

            return dto;
        }

    private String xacDinhMienCuaDai(String tenDai, LocalDate ngay) {
        // Dò trong miền Bắc
        if (bacRepo.findAllByNgay(ngay).stream().anyMatch(kq -> tenDai.equalsIgnoreCase((String) getField(kq, "getTenDai")))) {
            return "MIỀN BẮC";
        }

        // Miền Trung
        if (trungRepo.findAllByNgay(ngay).stream().anyMatch(kq -> tenDai.equalsIgnoreCase((String) getField(kq, "getTenDai")))) {
            return "MIỀN TRUNG";
        }

        // Miền Nam
        if (namRepo.findAllByNgay(ngay).stream().anyMatch(kq -> tenDai.equalsIgnoreCase((String) getField(kq, "getTenDai")))) {
            return "MIỀN NAM";
        }

        return "KHÔNG RÕ";
    }

    private <T> T getField(Object obj, String methodName) {
        try {
            return (T) obj.getClass().getMethod(methodName).invoke(obj);
        } catch (Exception e) {
            throw new RuntimeException("Lỗi phản xạ khi lấy field: " + methodName);
        }
    }
}