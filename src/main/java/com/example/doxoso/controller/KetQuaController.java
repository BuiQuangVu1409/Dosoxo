package com.example.doxoso.controller;

import com.example.doxoso.model.*;
import com.example.doxoso.repository.KetQuaMienBacRepository;
import com.example.doxoso.repository.KetQuaMienNamRepository;
import com.example.doxoso.repository.KetQuaMienTrungRepository;
import com.example.doxoso.repository.SoNguoiChoiRepository;
import com.example.doxoso.service.DanhSachDaiTheoMienService;
import com.example.doxoso.service.KetQuaService;
import com.example.doxoso.service.KiemTraKetQuaService;

import com.example.doxoso.service.LichQuayXoSoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/xoso")
public class KetQuaController {

    @Autowired
    private KetQuaService ketQuaService;

    @Autowired
    private KiemTraKetQuaService kiemTraKetQuaService;

    @Autowired
    private SoNguoiChoiRepository soNguoiChoiRepository;
    @Autowired
    private KetQuaMienBacRepository bacRepo;

    @Autowired
    private KetQuaMienTrungRepository trungRepo;

    @Autowired
    private KetQuaMienNamRepository namRepo;

//GIAO DIỆN


        @PostMapping("/doiso")
        public Object doSo(@RequestBody SoNguoiChoi so) {
            return kiemTraKetQuaService.kiemTraSo(so);
        }



    @GetMapping("/ketqua")
    public List<KetQuaTheoDaiDto> layKetQuaTheoNgay(@RequestParam("ngay") String ngayStr) {
        LocalDate ngay;
        try {
            ngay = LocalDate.parse(ngayStr);
        } catch (DateTimeParseException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ngày không hợp lệ: " + ngayStr);
        }

        List<KetQuaTheoDaiDto> ketQua = new ArrayList<>();

        List<KetQuaMienBac> mb = bacRepo.findAllByNgay(ngay);
        if (!mb.isEmpty()) {
            ketQua.add(new KetQuaTheoDaiDto("MIỀN BẮC", "HÀ NỘI", mb));
        }

        trungRepo.findAllByNgay(ngay).stream()
                .collect(Collectors.groupingBy(KetQuaMienTrung::getTenDai))
                .forEach((tenDai, ds) -> ketQua.add(new KetQuaTheoDaiDto("MIỀN TRUNG", tenDai, ds)));

        namRepo.findAllByNgay(ngay).stream()
                .collect(Collectors.groupingBy(KetQuaMienNam::getTenDai))
                .forEach((tenDai, ds) -> ketQua.add(new KetQuaTheoDaiDto("MIỀN NAM", tenDai, ds)));

        return ketQua;
    }



//




    /**
     * Trả về danh sách đã đối chiếu tất cả số người chơi
     */
    @GetMapping("/doi-chieu")
    public List<DoiChieuKetQuaDto> doiChieuTatCa() {
        return ketQuaService.doiChieuTatCaSo();
    }

    /**
     * Trả về danh sách số trúng
     */
    @GetMapping("/trung")
    public List<DoiChieuKetQuaDto> danhSachTrung() {
        return ketQuaService.layDanhSachSoTrung();
    }

    /**
     * Trả về danh sách số trật
     */
    @GetMapping("/trat")
    public List<DoiChieuKetQuaDto> danhSachTrat() {
        return ketQuaService.layDanhSachSoTrat();
    }

    /**
     * Thêm một số người chơi mới (POST JSON)
     */
    @PostMapping("/them-so")
    public SoNguoiChoi themSoNguoiChoi(@RequestBody SoNguoiChoi so) {
        return soNguoiChoiRepository.save(so);
    }

    /**
     * Đối chiếu 1 số người chơi cụ thể (POST JSON)
     */
    @PostMapping("/doi-chieu")
    public DoiChieuKetQuaDto doiChieuMotSo(@RequestBody SoNguoiChoi so) {
        return kiemTraKetQuaService.kiemTraSo(so);
    }


    @GetMapping("/{ketqua}/{cachdanh}")
    public List<DoiChieuKetQuaDto> locTheoKetQuaVaCachDanh(
            @PathVariable("ketqua") String ketqua,
            @PathVariable("cachdanh") String cachDanh
    ) {
        return ketQuaService.locTheoKetQuaVaCachDanh(ketqua, cachDanh);
    }







}
