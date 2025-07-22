    package com.example.doxoso.service;

    import com.example.doxoso.model.SoNguoiChoi;
    import com.example.doxoso.repository.KetQuaMienBacRepository;
    import com.example.doxoso.repository.KetQuaMienNamRepository;
    import com.example.doxoso.repository.KetQuaMienTrungRepository;
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.stereotype.Service;
    import java.util.List;
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

        public List<String> xacDinhDanhSachDaiCanDo(SoNguoiChoi so) {
            String thu = chuyenDoiNgayService.chuyenDoiThu(so.getNgay());

            if (so.getTenDai() != null && !so.getTenDai().isBlank()) {
                return List.of(so.getTenDai().toUpperCase());
            }

            return danhSachDaiTheoMienService.layDanhSachDaiTheoThuVaMien(thu, so.getMien());
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

        private <T> T getField(Object obj, String methodName) {
            try {
                return (T) obj.getClass().getMethod(methodName).invoke(obj);
            } catch (Exception e) {
                throw new RuntimeException("Lỗi phản xạ khi lấy field: " + methodName);
            }
        }
    }

