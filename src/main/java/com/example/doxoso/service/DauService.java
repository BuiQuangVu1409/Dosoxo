    package com.example.doxoso.service;

    import com.example.doxoso.model.DoiChieuKetQuaDto;
    import com.example.doxoso.repository.KetQuaMienBacRepository;
    import com.example.doxoso.repository.KetQuaMienTrungRepository;
    import com.example.doxoso.repository.KetQuaMienNamRepository;
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.stereotype.Service;
    import java.text.Normalizer;
    import java.time.DayOfWeek;
    import java.time.LocalDate;
    import java.util.*;

    @Service
    public class DauService {

        @Autowired
        private KetQuaMienBacRepository bacRepo;

        @Autowired
        private KetQuaMienTrungRepository trungRepo;

        @Autowired
        private KetQuaMienNamRepository namRepo;

        public DoiChieuKetQuaDto xuLyDau(String soDanh, String mien, LocalDate ngay, String tienDanh) {
            DoiChieuKetQuaDto dto = new DoiChieuKetQuaDto();
            dto.setSoDanh(soDanh);
            dto.setMien(mien);
            dto.setNgay(ngay);
            dto.setThu(chuyenNgaySangThu(ngay));
            dto.setCachDanh("DAU");
            dto.setTienDanh(tienDanh);
            dto.setDanhSachDai(new ArrayList<>());

            if (tienDanh == null) {
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
                var ketQua = bacRepo.findAllByNgay(ngay).stream()

                        .filter(kq -> kq.getGiai() != null && "G7".equalsIgnoreCase(kq.getGiai()))

                        .toList();

                Set<String> tatCaDai = new HashSet<>();
                for (var kq : ketQua) {
                    tatCaDai.add(kq.getTenDai());

                    // So sánh trực tiếp từng dòng G7 – mỗi dòng là 1 cặp số như "78"
                    if (soChuan.equals(chuanHoa(kq.getSoTrung()))) {
                        dto.setTrung(true);
                        dto.setGiaiTrung("Giải 7");
                        dto.setTenDai(kq.getTenDai());
                        dto.setCachTrung("Trúng ĐẦU MIỀN BẮC – Đài " + kq.getTenDai());
                        dto.setTienTrung(Double.parseDouble(tienDanh) * 1000);
                        dto.setDanhSachDai(List.copyOf(tatCaDai));
                        return dto;
                    }
                }

                dto.setTrung(false);
                dto.setTienTrung(0);
                dto.setSaiLyDo(List.of("Không trúng ĐẦU MIỀN BẮC – Giải 7"));
                dto.setDanhSachDai(List.copyOf(tatCaDai));
                return dto;
            }

            if (mienChuan.equals("MIENTRUNG")) {
                var ketQua = trungRepo.findAllByNgay(ngay).stream()
                        .filter(kq -> kq.getGiai() != null && "G8".equalsIgnoreCase(kq.getGiai()))
                        .toList();
                for (var kq : ketQua) {
                    dto.getDanhSachDai().add(kq.getTenDai());
                    if (soChuan.equals(chuanHoa(kq.getSoTrung()))) {
                        dto.setTrung(true);
                        dto.setGiaiTrung("Giải 8");
                        dto.setTenDai(kq.getTenDai());
                        dto.setCachTrung("Trúng ĐẦU MIỀN TRUNG – Đài " + kq.getTenDai());
                        dto.setTienTrung(Double.parseDouble(tienDanh) * 1000);
                        return dto;
                    } else {
                        saiLyDo.add("Trật đài " + kq.getTenDai());
                    }
                }
            }

            if (mienChuan.equals("MIENNAM")) {
                var ketQua = namRepo.findAllByNgay(ngay).stream()

                        .filter(kq -> kq.getGiai() != null && "G8".equalsIgnoreCase(kq.getGiai()))
                        .toList();


                for (var kq : ketQua) {
                    dto.getDanhSachDai().add(kq.getTenDai());
                    if (soChuan.equals(chuanHoa(kq.getSoTrung()))) {
                        dto.setTrung(true);
                        dto.setGiaiTrung("Giải 8");
                        dto.setTenDai(kq.getTenDai());
                        dto.setCachTrung("Trúng ĐẦU MIỀN NAM – Đài " + kq.getTenDai());
                        dto.setTienTrung(Double.parseDouble(tienDanh) * 1000);
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
            return cachDanhChuanHoa != null && cachDanhChuanHoa.contains("DAU");
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


//        private List<String> laySoTrungTheoGiaiVaDai(LocalDate ngay, String tenGiai, String tenDaiChuan, String mienChuan) {
//            return switch (mienChuan) {
//                case "MIỀN BẮC" -> bacRepo.findAllByNgay(ngay).stream()
//                        .filter(kq -> tenGiai.equalsIgnoreCase(kq.getGiai()) &&
//                                chuanHoa(kq.getTenDai()).equals(tenDaiChuan))
//                        .map(kq -> kq.getSoTrung())
//                        .toList();
//
//                case "MIỀN TRUNG" -> trungRepo.findAllByNgay(ngay).stream()
//                        .filter(kq -> tenGiai.equalsIgnoreCase(kq.getGiai()) &&
//                                chuanHoa(kq.getTenDai()).equals(tenDaiChuan))
//                        .map(kq -> kq.getSoTrung())
//                        .toList();
//
//                case "MIỀN NAM" -> namRepo.findAllByNgay(ngay).stream()
//                        .filter(kq -> tenGiai.equalsIgnoreCase(kq.getGiai()) &&
//                                chuanHoa(kq.getTenDai()).equals(tenDaiChuan))
//                        .map(kq -> kq.getSoTrung())
//                        .toList();
//
//                default -> List.of();
//            };
//        }

    }
