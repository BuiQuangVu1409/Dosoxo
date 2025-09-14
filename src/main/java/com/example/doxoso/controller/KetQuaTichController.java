package com.example.doxoso.controller;

// com.example.doxoso.controller.KetQuaTichController.java



import com.example.doxoso.model.KetQuaTich;
import com.example.doxoso.repository.SoNguoiChoiRepository;
import com.example.doxoso.service.KetQuaTichService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/ket-qua-tich")
public class KetQuaTichController {

    private final KetQuaTichService service;
    private final SoNguoiChoiRepository soNguoiChoiRepository;

    /** Chạy & lưu 3 miền cho 1 player trong 1 ngày */
    @PostMapping("/run-save/{playerId}/{ngay}")
    public List<KetQuaTich> runSaveOne(
            @PathVariable Long playerId,
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate ngay,
            @RequestParam(required = false) String playerName
    ){
        return service.runAndSaveForPlayer(playerId, playerName, ngay);
    }

    @PostMapping("/run-save-all/{ngay}")
    public Map<Long, List<KetQuaTich>> runSaveAll(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate ngay
    ){
        // Lấy trực tiếp danh sách playerId trong ngày
        var ids = soNguoiChoiRepository.findDistinctPlayerIdsByNgay(ngay);

        Map<Long, List<KetQuaTich>> out = new LinkedHashMap<>();
        for (Long pid : ids){
            out.put(pid, service.runAndSaveForPlayer(pid, null, ngay));
        }
        return out;
    }


    /** Xem snapshot đã lưu (3 dòng/miền) cho 1 player */
    @GetMapping("/{playerId}/{ngay}")
    public List<KetQuaTich> getSaved(
            @PathVariable Long playerId,
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate ngay
    ){
        return service.findByPlayerAndNgay(playerId, ngay);
    }
}
