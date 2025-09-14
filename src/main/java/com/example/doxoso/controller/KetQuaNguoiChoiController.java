package com.example.doxoso.controller;

import com.example.doxoso.model.KetQuaNguoiChoi;
import com.example.doxoso.service.KetQuaNguoiChoiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/ketqua")
public class KetQuaNguoiChoiController {
    @Autowired
    private  KetQuaNguoiChoiService ketQuaNguoiChoiService;

    public KetQuaNguoiChoiController(KetQuaNguoiChoiService service) {
        this.ketQuaNguoiChoiService = service;
    }

    // Lấy theo playerId
    @GetMapping("/player/{playerId}")
    public List<KetQuaNguoiChoi> getByPlayerId(@PathVariable Long playerId) {
        return ketQuaNguoiChoiService.getByPlayerId(playerId);
    }

    // Lấy theo playerName
    @GetMapping("/name/{playerName}")
    public List<KetQuaNguoiChoi> getByPlayerName(@PathVariable String playerName) {
        return ketQuaNguoiChoiService.getByPlayerName(playerName);
    }

    // Lấy theo ngày chơi

    @GetMapping("/ngay/{ngayChoi}")
    public List<KetQuaNguoiChoi> getByNgayChoi(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate ngayChoi) {
        return ketQuaNguoiChoiService.getByNgayChoi(ngayChoi);
    }

    // Lấy theo playerId + ngày
//    http://localhost:8080/api/ketqua/player/2/ngay/2025-06-25
    @GetMapping("/player/{playerId}/ngay/{ngay}")
    public List<KetQuaNguoiChoi> getByPlayerIdAndNgay(
            @PathVariable Long playerId,
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate ngay) {
        return ketQuaNguoiChoiService.getByPlayerIdAndNgay(playerId, ngay);
    }

    // Lấy theo playerName + ngày
//    GET http://localhost:8080/api/ketqua/player/Đại Tâm/2025-08-23
    @GetMapping("/player/{playerName}/{ngayChoi}")
    public List<KetQuaNguoiChoi> getByPlayerNameAndNgay(
            @PathVariable String playerName,
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate ngayChoi) {
        return ketQuaNguoiChoiService.getByPlayerNameAndNgay(playerName, ngayChoi);
    }



    // API lấy kết quả theo khoảng ngày
    //    http://localhost:8080/api/ketqua/theo-khoang-ngay?from=2025-08-01&to=2025-08-15

    @GetMapping("/theo-khoang-ngay")
    public ResponseEntity<List<KetQuaNguoiChoi>> getByRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        List<KetQuaNguoiChoi> list = ketQuaNguoiChoiService.getKetQuaTrongKhoang(startDate, endDate);
        return ResponseEntity.ok(list);
    }


}
