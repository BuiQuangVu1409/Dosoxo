package com.example.doxoso.controller;


import com.example.doxoso.model.DoiChieuKetQuaDto;
import com.example.doxoso.model.Player;
import com.example.doxoso.model.PlayerDoKetQuaDto;
import com.example.doxoso.model.SoNguoiChoi;
import com.example.doxoso.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/player")
public class PlayerController {

    @Autowired
    private PlayerService playerService;

    @Autowired
    private SoNguoiChoiService soNguoiChoiService;

    @Autowired
    private KiemTraKetQuaService kiemTraKetQuaService;

    @Autowired
    private KetQuaService ketQuaService;
    // Tạo mới Player
    @PostMapping
    public ResponseEntity<?> addPlayer(@RequestBody Player player) {
        try {
            Player saved = playerService.addPlayer(player);
            return ResponseEntity.ok(saved);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(
                    Map.of(
                            "error", "Trùng ID",
                            "message", ex.getMessage(),
                            "suggestion", "Dùng API PUT /players/{id} để thay thế dữ liệu cũ"
                    )
            );
        }
    }
    // Lấy tất cả Player

    @GetMapping({"", "/"})
    public ResponseEntity<List<Player>> getAllPlayers() {
        return ResponseEntity.ok(playerService.getAllPlayers());
    }

    // Lấy theo id / trả về kết quả dò theo playerId
    @GetMapping({"/{playerId}", "/{playerId}/"})
    public ResponseEntity<?> getByPlayerId(@PathVariable Long playerId) {
        List<SoNguoiChoi> list = soNguoiChoiService.getSoNguoiChoiByPlayerId(playerId);
        if (list == null || list.isEmpty()) {
            return ResponseEntity.status(404).body("Không tìm thấy dữ liệu cho playerId = " + playerId);
        }

        var player = list.get(0).getPlayer();

        // ✅ Dùng service trung gian
        List<DoiChieuKetQuaDto> ketQua = ketQuaService.doKetQua(list);

        PlayerDoKetQuaDto response = new PlayerDoKetQuaDto();
        response.setPlayerId(player.getId());
        response.setName(player.getName());
        response.setHoaHong(player.getHoaHong());
        response.setHeSoCachDanh(player.getHeSoCachDanh());
        response.setKetQua(ketQua);

        return ResponseEntity.ok(response);
    }


    @PutMapping("/{id}")
        public ResponseEntity<?> replacePlayer (
                @PathVariable Long id,
                @RequestBody Player player){
            Player updated = playerService.replacePlayer(id, player);
            return ResponseEntity.ok(updated);
        }
        // Xoá Player
        @DeleteMapping("/{id}")
        public ResponseEntity<Void> deletePlayer (@PathVariable Long id){
            playerService.deletePlayer(id);
            return ResponseEntity.noContent().build();
        }

        // Cập nhật hoa hồng riêng
        @PatchMapping("/{id}/hoahong")
        public ResponseEntity<Player> updateHoaHong (@PathVariable Long id, @RequestParam Double hoaHong){
            return ResponseEntity.ok(playerService.updateHoaHong(id, hoaHong));
        }
//    http://localhost:8080/api/player/10/hoahong?hoaHong=69.5
        // Cập nhật hệ số cách đánh riêng
        @PatchMapping("/{id}/heso")
        public ResponseEntity<Player> updateHeSo (@PathVariable Long id, @RequestParam Double heSo){
            return ResponseEntity.ok(playerService.updateHeSoCachDanh(id, heSo));
        }
//   http://localhost:8080/api/player/10/heso?heSo=2.5

    }


