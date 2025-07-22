package com.example.doxoso.service;

import com.example.doxoso.model.*;
import com.example.doxoso.repository.KetQuaMienBacRepository;
import com.example.doxoso.repository.KetQuaMienNamRepository;
import com.example.doxoso.repository.KetQuaMienTrungRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.text.Normalizer;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


@Service
public class KiemTraKetQuaService {
    @Autowired
    private KetQuaMienTrungRepository trungRepo;
    @Autowired
    private KetQuaMienBacRepository bacRepo;
    @Autowired
    private KetQuaMienNamRepository namRepo;
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
            List<Object> danhSachKetQua = new ArrayList<>();
            danhSachKetQua.addAll(trungRepo.findAllByNgay(so.getNgay()));
            danhSachKetQua.addAll(bacRepo.findAllByNgay(so.getNgay()));
            danhSachKetQua.addAll(namRepo.findAllByNgay(so.getNgay()));
            baChanService.xuLyBaChan(dto, so.getSoDanh(), so.getTienDanh(), so.getMien(), danhSachKetQua);
            return dto;
        }

        // 2 chân
        if (cachDanhChuanHoa.equals("2CHAN")) {
            dto.setTienDanh(so.getTienDanh());
            boolean trung = haiChanService.kiemTraTrung2Chan(so);
            dto.setCachTrung("2 chân");
            if (trung) {
                dto.setTrung(true);
                dto.setGiaiTrung("Trúng 2 chân");
                dto.setTienTrung(tinhTienService.tinhTienTrung("2CHAN", so.getTienDanh()));
                dto.setSaiLyDo(null);
            } else {
                dto.setTrung(false);
                dto.setTienTrung(0);
                dto.setSaiLyDo(List.of("Không trúng 2 chân"));
            }
            return dto;
        }

        // Xuyên
        if (xuyenService.laCachDanhXuyen(cachDanhChuanHoa)) {
            dto.setTienDanh(so.getTienDanh());
            Optional<String> tenDaiTrung = xuyenService.xuLyTrungXuyen(cachDanhChuanHoa, so.getSoDanh(), so.getNgay(), so.getMien());
            dto.setCachTrung(so.getCachDanh());
            if (tenDaiTrung.isPresent()) {
                dto.setTrung(true);
                dto.setGiaiTrung("Trúng " + so.getCachDanh() + " tại " + tenDaiTrung.get());
                dto.setTienTrung(tinhTienService.tinhTienTrung(so.getCachDanh(), so.getTienDanh()));
                dto.setTenDai(tenDaiTrung.get());
            } else {
                dto.setTrung(false);
                dto.setTienTrung(0);
                dto.setSaiLyDo(List.of("Không trúng " + so.getCachDanh()));
            }
            return dto;
        }


        // ĐẦU
        if (cachDanhChuanHoa.equals("DAU")) {
            DoiChieuKetQuaDto ketQuaDau = dauService.xuLyDau(
                    so.getSoDanh(),
                    so.getMien(),
                    so.getNgay(),
                    so.getTienDanh()
            );
            dto.setTrung(ketQuaDau.isTrung());
            dto.setGiaiTrung(ketQuaDau.getGiaiTrung());
            dto.setTienTrung(ketQuaDau.getTienTrung());
            dto.setCachTrung("ĐẦU");
            dto.setSaiLyDo(ketQuaDau.getSaiLyDo());
            dto.setTenDai(ketQuaDau.getTenDai());
            return dto;
        }
// DUÔI
        if(cachDanhChuanHoa.equals("DUOI")){
            DoiChieuKetQuaDto ketQuaDuoi = duoiService.xuLyDuoi(
                    so.getSoDanh(),
                    so.getMien(),
                    so.getNgay(),
                    so.getTienDanh()
            );
            dto.setTrung(ketQuaDuoi.isTrung());
            dto.setGiaiTrung((ketQuaDuoi.getGiaiTrung()));
            dto.setTienTrung(ketQuaDuoi.getTienTrung());
            dto.setCachTrung("ĐUÔI");
            dto.setSaiLyDo(ketQuaDuoi.getSaiLyDo());
            dto.setTenDai(ketQuaDuoi.getTenDai());
            return dto;
        }
        // ĐẦU ĐUÔI
        if (cachDanhChuanHoa.equals("DAUDUOI")) {
            DoiChieuKetQuaDto ketQuaDauDuoi = dauDuoiService.xuLyDauDuoi(
                    so.getSoDanh(),
                    so.getMien(),
                    so.getNgay(),
                    so.getTienDanh()
            );
            dto.setTrung(ketQuaDauDuoi.isTrung());
            dto.setGiaiTrung(ketQuaDauDuoi.getGiaiTrung());
            dto.setTienTrung(ketQuaDauDuoi.getTienTrung());
            dto.setCachTrung("ĐẦU ĐUÔI");
            dto.setSaiLyDo(ketQuaDauDuoi.getSaiLyDo());
            dto.setTenDai(ketQuaDauDuoi.getTenDai());
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

