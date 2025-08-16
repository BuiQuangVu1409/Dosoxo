package com.example.doxoso.service;

import com.example.doxoso.model.*;
import com.example.doxoso.repository.KetQuaMienBacRepository;
import com.example.doxoso.repository.KetQuaMienNamRepository;
import com.example.doxoso.repository.KetQuaMienTrungRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.DecimalFormat;
import java.text.Normalizer;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;


@Service
public class KiemTraKetQuaService {

    @Autowired
    private TinhTienService tinhTienService;
    @Autowired
    private ChuyenDoiNgayService chuyenDoiNgayService;
    @Autowired
    private DanhSachDaiTheoMienService danhSachDaiTheoMienService;
    @Autowired
    private XuyenService xuyenService;
    @Autowired
    private HaiChanService haiChanService;
    @Autowired
    private BaChanService baChanService;
    @Autowired
    private DauService dauService;
    @Autowired
    private DuoiService duoiService;
    @Autowired
    private DauDuoiService dauDuoiService;
    @Autowired
    private LonService lonService;
    @Autowired
    private NhoService nhoService;





    public DoiChieuKetQuaDto kiemTraSo(SoNguoiChoi so) {
        DoiChieuKetQuaDto dto = new DoiChieuKetQuaDto();

        if (so == null || so.getNgay() == null) {
            throw new IllegalArgumentException("Thông tin người chơi không hợp lệ");
        }

        String thu = chuyenDoiNgayService.chuyenDoiThu(so.getNgay());
        dto.setThu(thu);
        dto.setSoDanh(so.getSoDanh());
        dto.setTenDai(so.getTenDai());
        dto.setMien(so.getMien());
        dto.setNgay(so.getNgay());
        dto.setCachDanh(so.getCachDanh());
        dto.setTienDanh(so.getTienDanh());
        List<String> danhSachDai = layDanhSachDaiTuCachDanh(so);


        dto.setDanhSachDai(danhSachDai);
        String cachDanhChuanHoa = chuanHoaCachDanhTheoMien(so.getCachDanh());


        // 3 chân
        if (cachDanhChuanHoa.equals("3CHAN")) {
            dto.setTienDanh(so.getTienDanh());
            dto.setCachTrung("3 chân");

            // Dò chi tiết kết quả 3 chân
            DoiChieuKetQuaDto ketQuaChiTiet = baChanService.xuLyBaChan(so);

            // Gán kết quả chi tiết từng đài
            dto.setKetQuaTungDai(ketQuaChiTiet.getKetQuaTungDai());

            // Gán danh sách đài
            dto.setDanhSachDai(
                    ketQuaChiTiet.getKetQuaTungDai().stream()
                            .map(DoiChieuKetQuaDto.KetQuaTheoDai::getTenDai)
                            .collect(Collectors.toList())
            );

            // Kiểm tra trúng
            if (ketQuaChiTiet.isTrung()) {
                dto.setTrung(true);

                // ✅ Tính tiền từng miền cho 3 chân (bao lô, thưởng, đặc biệt)
                double tongTien = 0;
                double tongBaoLo = 0;
                double tongThuong = 0;
                double tongDacBiet = 0;

                for (DoiChieuKetQuaDto.KetQuaTheoDai dai : ketQuaChiTiet.getKetQuaTungDai()) {
                    if (dai.isTrung()) {
                        double[] tienTrung = tinhTienService.tinhTien3Chan(
                                so.getTienDanh(),
                                dai.getMien(),
                                dai.getGiaiTrung()
                        );

                        // ✅ Làm tròn tiền từng đài
                        dai.setTienTrung((tienTrung[0]));

                        tongTien += tienTrung[0];
                        tongBaoLo += tienTrung[1];
                        tongThuong += tienTrung[2];
                        tongDacBiet += tienTrung[3];
                    } else {
                        dai.setTienTrung(0.0);
                    }
                }

                // ✅ Gán tiền sau khi làm tròn
                dto.setTienTrung((tongTien));
                dto.setTienTrungBaoLo((tongBaoLo));
                dto.setTienTrungThuong((tongThuong));
                dto.setTienTrungDacBiet((tongDacBiet));

                // Gán thông tin giải trúng
                dto.setGiaiTrung(
                        ketQuaChiTiet.getKetQuaTungDai().stream()
                                .filter(DoiChieuKetQuaDto.KetQuaTheoDai::isTrung)
                                .map(dai -> dai.getTenDai() + " (" + dai.getSoLanTrung() + " lần)")
                                .collect(Collectors.joining(", "))
                );

                dto.setSaiLyDo(null);
            } else {
                dto.setTrung(false);
                dto.setTienTrung(0.0);
                dto.setTienTrungBaoLo(0.0);
                dto.setTienTrungThuong(0.0);
                dto.setTienTrungDacBiet(0.0);
                dto.setGiaiTrung(null);
                dto.setSaiLyDo(List.of("Không trúng 3 chân"));
            }

            return dto;
        }



//        if (cachDanhChuanHoa.equals("3CHAN")) {
//            dto.setTienDanh(so.getTienDanh());
//            List<Object> danhSachKetQua = new ArrayList<>();
//            danhSachKetQua.addAll(trungRepo.findAllByNgay(so.getNgay()));
//            danhSachKetQua.addAll(bacRepo.findAllByNgay(so.getNgay()));
//            danhSachKetQua.addAll(namRepo.findAllByNgay(so.getNgay()));
//            baChanService.xuLyBaChan(dto, so.getSoDanh(), so.getTienDanh(), so.getMien(), danhSachKetQua);
//            return dto;
//        }

        // 2 chân
//        if (cachDanhChuanHoa.equals("2CHAN")) {
//            dto.setTienDanh(so.getTienDanh());
//            dto.setCachTrung("2 chân");
//
//            // Dò chi tiết kết quả 2 chân
//            DoiChieuKetQuaDto ketQuaChiTiet = haiChanService.traVeKetQuaChiTiet2Chan(so);
//
//            // Gán kết quả chi tiết
//            dto.setKetQuaTungDai(ketQuaChiTiet.getKetQuaTungDai());
//
//            // Gán danh sách đài
//            dto.setDanhSachDai(
//                    ketQuaChiTiet.getKetQuaTungDai().stream()
//                            .map(DoiChieuKetQuaDto.KetQuaTheoDai::getTenDai)
//                            .collect(Collectors.toList())
//            );
//
//            if (ketQuaChiTiet.isTrung()) {
//                dto.setTrung(true);
//
//                // ✅ Tính tổng tiền trúng dựa theo số lần trúng mỗi đài
//                double tongTien = 0;
//                for (DoiChieuKetQuaDto.KetQuaTheoDai dai : ketQuaChiTiet.getKetQuaTungDai()) {
//                    if (dai.isTrung()) {
////                        tongTien += tinhTienService.tinhTongTien2Chan(
////                                so.getMien(),
////                                Double.parseDouble(so.getTienDanh()),
////                                dai.getSoLanTrung()
//                        tongTien += tinhTienService.tinhTongTien2Chan(
//                                dai.getMien(),
//                                Double.parseDouble(so.getTienDanh()),
//                                dai.getSoLanTrung()
//                        );
//                    }
//                }
//                dto.setTienTrung(tongTien);
//
//                // ✅ Ghi rõ đài nào trúng bao nhiêu lần
//                dto.setGiaiTrung(
//                        ketQuaChiTiet.getKetQuaTungDai().stream()
//                                .filter(DoiChieuKetQuaDto.KetQuaTheoDai::isTrung)
//                                .map(dai -> dai.getTenDai() + " (" + dai.getSoLanTrung() + " lần)")
//                                .collect(Collectors.joining(", "))
//                );
//
//                dto.setSaiLyDo(null);
//            } else {
//                dto.setTrung(false);
//                dto.setTienTrung(0.0);
//                dto.setGiaiTrung(null);
//                dto.setSaiLyDo(List.of("Không trúng 2 chân"));
//            }
//
//            return dto;
//        }
        // XỬ LÝ 2 CHÂN
        if (cachDanhChuanHoa.equals("2CHAN")) {
            dto.setTienDanh(so.getTienDanh());
            dto.setCachTrung("2 chân");

            // Dò chi tiết kết quả 2 chân
            DoiChieuKetQuaDto ketQuaChiTiet = haiChanService.traVeKetQuaChiTiet2Chan(so);

            // Gán kết quả chi tiết
            dto.setKetQuaTungDai(ketQuaChiTiet.getKetQuaTungDai());

            // Gán danh sách đài
            dto.setDanhSachDai(
                    ketQuaChiTiet.getKetQuaTungDai().stream()
                            .map(DoiChieuKetQuaDto.KetQuaTheoDai::getTenDai)
                            .collect(Collectors.toList())
            );

            // Kiểm tra có trúng hay không
            if (ketQuaChiTiet.isTrung()) {
                dto.setTrung(true);

                double tongTien = 0;

                // ✅ Tính tiền riêng cho từng đài, rồi cộng tổng
                for (DoiChieuKetQuaDto.KetQuaTheoDai dai : ketQuaChiTiet.getKetQuaTungDai()) {
                    if (dai.isTrung()) {
                        double tienTrung = tinhTienService.tinhTongTien2Chan(
                                dai.getMien(),                          // Tính theo MIỀN của từng đài
                                Double.parseDouble(so.getTienDanh()),  // Tiền đánh
                                dai.getSoLanTrung()                    // Số lần trúng
                        );
                        dai.setTienTrung(tienTrung); // ✅ Gán tiền riêng vào từng đài
                        tongTien += tienTrung;       // ✅ Cộng vào tổng
                    } else {
                        dai.setTienTrung(0.0); // ✅ Đồng nhất dữ liệu
                    }
                }

                dto.setTienTrung(tongTien);

                // ✅ Ghi rõ đài nào trúng bao nhiêu lần
                dto.setGiaiTrung(
                        ketQuaChiTiet.getKetQuaTungDai().stream()
                                .filter(DoiChieuKetQuaDto.KetQuaTheoDai::isTrung)
                                .map(dai -> dai.getTenDai() + " (" + dai.getSoLanTrung() + " lần)")
                                .collect(Collectors.joining(", "))
                );

                dto.setSaiLyDo(null);
            } else {
                dto.setTrung(false);
                dto.setTienTrung(0.0);
                dto.setGiaiTrung(null);
                dto.setSaiLyDo(List.of("Không trúng 2 chân"));
            }

            return dto;
        }





        // Xuyên
//        if (xuyenService.laCachDanhXuyen(cachDanhChuanHoa)) {
//            dto.setTienDanh(so.getTienDanh());
//            Optional<String> tenDaiTrung = xuyenService.xuLyTrungXuyen(cachDanhChuanHoa, so.getSoDanh(), so.getNgay(), so.getMien());
//            dto.setCachTrung(so.getCachDanh());
//            if (tenDaiTrung.isPresent()) {
//                dto.setTrung(true);
//                dto.setGiaiTrung("Trúng " + so.getCachDanh() + " tại " + tenDaiTrung.get());
//              // tính tiền ở tinh tien service
//                dto.setTienTrung(
//                        tinhTienService.tinhTienTrung("XUYEN2", so.getTienDanh(), so.getMien())
//                );
//
//                dto.setTenDai(tenDaiTrung.get());
//            } else {
//                dto.setTrung(false);
//                dto.setTienTrung(0);
//                dto.setSaiLyDo(List.of("Không trúng " + so.getCachDanh()));
//            }
//            return dto;
//        }
        if (xuyenService.laCachDanhXuyen(cachDanhChuanHoa)) {
            dto.setTienDanh(so.getTienDanh());
            Optional<String> tenDaiTrung = xuyenService.xuLyTrungXuyen(cachDanhChuanHoa, so.getSoDanh(), so.getNgay(), so.getMien());
            dto.setCachTrung(so.getCachDanh());
            if (tenDaiTrung.isPresent()) {
                dto.setTrung(true);
                dto.setGiaiTrung("Trúng " + so.getCachDanh() + " tại " + tenDaiTrung.get());

                // ✅ tính tiền đúng loại XUYÊN
                dto.setTienTrung(
                        tinhTienService.tinhTienTrung(cachDanhChuanHoa, so.getTienDanh(), so.getMien())
                );

                dto.setTenDai(tenDaiTrung.get());
            } else {
                dto.setTrung(false);
                dto.setTienTrung(Double.valueOf(0));

                dto.setSaiLyDo(List.of("Không trúng " + so.getCachDanh()));
            }
            return dto;
        }


        // ĐẦU

        if (cachDanhChuanHoa.equals("DAU")) {
            DoiChieuKetQuaDto ketQuaDau = dauService.xuLyDau(
                    so.getSoDanh(),                   // String
                    so.getMien(),                     // String
                    so.getNgay(),                     // LocalDate
                    so.getTienDanh(), // String
                    so.getTenDai()
            );

            dto.setTrung(ketQuaDau.isTrung());
            dto.setGiaiTrung(ketQuaDau.getGiaiTrung());
            dto.setTienTrung(ketQuaDau.getTienTrung());
            dto.setCachTrung("ĐẦU");
            dto.setSaiLyDo(ketQuaDau.getSaiLyDo());
            dto.setTenDai(ketQuaDau.getTenDai());
            dto.setDanhSachDai(ketQuaDau.getDanhSachDai());
            dto.setGhiChu(ketQuaDau.getGhiChu());
            dto.setKetQuaTungDai(ketQuaDau.getKetQuaTungDai());
            return dto;
        }






// DUÔI
        if (cachDanhChuanHoa.equals("DUOI")) {
            DoiChieuKetQuaDto ketQuaDuoi = duoiService.xuLyDuoi(
                    so.getSoDanh(),
                    so.getMien(),
                    so.getNgay(),
                    so.getTienDanh(),
                    so.getTenDai() // thêm dòng này để truyền tên đài người chơi nhập
            );
            dto.setTrung(ketQuaDuoi.isTrung());
            dto.setGiaiTrung(ketQuaDuoi.getGiaiTrung());
            dto.setTienTrung(ketQuaDuoi.getTienTrung());
            dto.setCachTrung(ketQuaDuoi.getCachTrung());
            dto.setSaiLyDo(ketQuaDuoi.getSaiLyDo());
            dto.setTenDai(ketQuaDuoi.getTenDai());
            dto.setDanhSachDai(ketQuaDuoi.getDanhSachDai());
            dto.setGhiChu(ketQuaDuoi.getGhiChu()); // nếu có ghi chú như "Bạn chỉ dò 1 phần đài"
            dto.setKetQuaTungDai(ketQuaDuoi.getKetQuaTungDai());
            return dto;
        }

// ĐẦU ĐUÔI
        if (cachDanhChuanHoa.equals("DAUDUOI")) {
            DoiChieuKetQuaDto ketQuaDauDuoi = dauDuoiService.xuLyDauDuoi(
                    so.getSoDanh(),
                    so.getMien(),
                    so.getNgay(),
                    so.getTienDanh(),
                    so.getTenDai()
            );
            dto.setTrung(ketQuaDauDuoi.isTrung());
            dto.setGiaiTrung(ketQuaDauDuoi.getGiaiTrung());
            dto.setTienTrung(ketQuaDauDuoi.getTienTrung());
            dto.setCachTrung(ketQuaDauDuoi.getCachTrung());
            dto.setSaiLyDo(ketQuaDauDuoi.getSaiLyDo());
            dto.setTenDai(ketQuaDauDuoi.getTenDai());
            dto.setDanhSachDai(ketQuaDauDuoi.getDanhSachDai());
            dto.setGhiChu(ketQuaDauDuoi.getGhiChu());
            dto.setKetQuaTungDai(ketQuaDauDuoi.getKetQuaTungDai());
            return dto;
        }

        // LỚN
        if (cachDanhChuanHoa.equals("LON")) {
            DoiChieuKetQuaDto ketQuaLon = lonService.xuLyLon(
                    so.getSoDanh(),
                    so.getMien(),
                    so.getTenDai(),
                    so.getNgay(),
                    so.getTienDanh()
            );

            dto.setTrung(ketQuaLon.isTrung());
            dto.setGiaiTrung(ketQuaLon.getGiaiTrung());
            dto.setTienTrung(ketQuaLon.getTienTrung());
            dto.setCachTrung("LỚN");
            dto.setSaiLyDo(ketQuaLon.getSaiLyDo());
            dto.setTenDai(ketQuaLon.getTenDai());
            return dto;
        }

// NHỎ
        if (cachDanhChuanHoa.equals("NHO")) {
            DoiChieuKetQuaDto ketQuaNho = nhoService.xuLyNho(
                    so.getSoDanh(),
                    so.getMien(),
                    so.getTenDai(),
                    so.getNgay(),
                    so.getTienDanh()
            );

            dto.setTrung(ketQuaNho.isTrung());
            dto.setGiaiTrung(ketQuaNho.getGiaiTrung());
            dto.setTienTrung(ketQuaNho.getTienTrung());
            dto.setCachTrung("NHỎ");
            dto.setSaiLyDo(ketQuaNho.getSaiLyDo());
            dto.setTenDai(ketQuaNho.getTenDai());
            return dto;
        }


        return dto;
    }



    private List<String> layDanhSachDaiTuCachDanh(SoNguoiChoi so) {
        int soLuongDai = tachSoLuongDai(so.getCachDanh());
        return danhSachDaiTheoMienService.layDanhSachDaiTheoSoLuong(so.getMien(), soLuongDai, so.getNgay(), chuyenDoiNgayService);
    }

    private int tachSoLuongDai(String cachDanh) {
        if (cachDanh == null) return 0;
        Matcher matcher = Pattern.compile("(\\d+)\\s*đài", Pattern.CASE_INSENSITIVE).matcher(cachDanh);
        return matcher.find() ? Integer.parseInt(matcher.group(1)) : 0;
    }

    private String chuanHoaCachDanhTheoMien(String cachDanh) {
        if (cachDanh == null) return "";
        return removeDiacritics(cachDanh)
                .toUpperCase()
                .trim()
                .replaceAll("\\s+", "");
    }


    private String removeDiacritics(String input) {
        if (input == null) return "";
        String normalized = Normalizer.normalize(input, Normalizer.Form.NFD);
        return normalized
                .replaceAll("\\p{InCombiningDiacriticalMarks}+", "") // remove dấu
                .replace('đ', 'd') // thay ký tự thường
                .replace('Đ', 'D'); // thay ký tự hoa
    }



}

