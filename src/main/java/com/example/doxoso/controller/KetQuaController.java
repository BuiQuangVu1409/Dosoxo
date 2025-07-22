package com.example.doxoso.controller;

import com.example.doxoso.model.DoiChieuKetQuaDto;
import com.example.doxoso.model.LichQuayXoSo;
import com.example.doxoso.model.SoNguoiChoi;
import com.example.doxoso.repository.SoNguoiChoiRepository;
import com.example.doxoso.service.DanhSachDaiTheoMienService;
import com.example.doxoso.service.KetQuaService;
import com.example.doxoso.service.KiemTraKetQuaService;

import com.example.doxoso.service.LichQuayXoSoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/xoso")
public class KetQuaController {

    @Autowired
    private KetQuaService ketQuaService;

    @Autowired
    private KiemTraKetQuaService kiemTraKetQuaService;

    @Autowired
    private SoNguoiChoiRepository soNguoiChoiRepository;

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
