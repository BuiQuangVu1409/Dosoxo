package com.example.doxoso.controller;

import com.example.doxoso.model.DoiChieuKetQuaDto;
import com.example.doxoso.model.PlayerDoKetQuaDto;
import com.example.doxoso.model.SoNguoiChoi;
import com.example.doxoso.service.KetQuaNguoiChoiService;
import com.example.doxoso.service.KetQuaService;
import com.example.doxoso.service.KiemTraKetQuaService;
import com.example.doxoso.service.SoNguoiChoiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
    @RequestMapping("/api/songuoichoi")
public class SoNguoiChoiController {

    @Autowired
    private SoNguoiChoiService soNguoiChoiService;

    @Autowired
    private KetQuaService ketQuaService;


    public SoNguoiChoiController(SoNguoiChoiService soNguoiChoiService) {
        this.soNguoiChoiService = soNguoiChoiService;
    }

    // ✅ Lấy theo id chính (soNguoiChoiId)
    @GetMapping("/{id}")
    public ResponseEntity<?> getSoNguoiChoiById(@PathVariable Long id) {
        try {
            SoNguoiChoi result = soNguoiChoiService.getSoNguoiChoiById(id);
            return ResponseEntity.ok(result);
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body("Không tìm thấy SoNguoiChoi với id = " + id);
        }
    }
    //======== lấy thông tin người chơi trên player id ===========//
    @GetMapping("/player/{playerId}")
    public ResponseEntity<?> getByPlayerId(@PathVariable Long playerId) {
        List<SoNguoiChoi> list = soNguoiChoiService.getSoNguoiChoiByPlayerId(playerId);

        if (list == null || list.isEmpty()) {
            return ResponseEntity.status(404).body("Không tìm thấy dữ liệu cho playerId = " + playerId);
        }

        // Lấy thông tin Player từ entity SoNguoiChoi (giả sử trong SoNguoiChoi có quan hệ tới Player)
        var player = list.get(0).getPlayer(); // hoặc gọi playerService.findById(playerId)

        // Dò kết quả từng số
        List<DoiChieuKetQuaDto> ketQua = ketQuaService.doKetQua(list);

        // Gói vào PlayerKetQuaDto
        PlayerDoKetQuaDto response = new PlayerDoKetQuaDto();
        response.setPlayerId(player.getId());
        response.setName(player.getName());
        response.setHoaHong(player.getHoaHong());
        response.setHeSoCachDanh(player.getHeSoCachDanh());
        response.setKetQua(ketQua);
        return ResponseEntity.ok(response);
    }
}
//    @PostMapping("/doketqua")
//    public List<DoiChieuKetQuaDto> doKetQua(@RequestBody List<SoNguoiChoi> soNguoiChoiList) {
//        return kiemTraKetQuaService.doKetQua(soNguoiChoiList);
//    }
//@PostMapping("/doketqua")
//public ResponseEntity<List<DoiChieuKetQuaDto>> doKetQua(
//        @RequestBody List<SoNguoiChoi> soNguoiChoiList,
//        @RequestParam(defaultValue = "false") boolean save) {
//
//    // 1) Dò
//    List<DoiChieuKetQuaDto> ketQuaList = kiemTraKetQuaService.doKetQua(soNguoiChoiList);
//
//    // 2) Lưu (nếu cần)
//    if (save) {
//        kiemTraKetQuaService.saveAllKetQua(soNguoiChoiList, ketQuaList);
//    }
//
//    // 3) Trả kết quả
//    return ResponseEntity.ok(ketQuaList);
//}


// ✅ Lấy tất cả số người chơi theo playerId


//    @GetMapping("/player/{playerId}")
//    public ResponseEntity<?> getByPlayerId(@PathVariable Long playerId) {
//        List<SoNguoiChoi> list = soNguoiChoiService.getSoNguoiChoiByPlayerId(playerId);
//
//        if (list == null || list.isEmpty()) {
//            return ResponseEntity.status(404).body("Không tìm thấy dữ liệu cho playerId = " + playerId);
//        }
//
//        // Gọi service dò số để trả về Dto kết quả đầy đủ
//        List<DoiChieuKetQuaDto> ketQua = kiemTraKetQuaService.doKetQua(list);
//
//        return ResponseEntity.ok(ketQua);
//    }
