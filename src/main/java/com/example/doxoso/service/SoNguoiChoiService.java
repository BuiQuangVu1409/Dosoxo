package com.example.doxoso.service;

import com.example.doxoso.model.SoNguoiChoi;
import com.example.doxoso.repository.SoNguoiChoiRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class SoNguoiChoiService {
    @Autowired
    private SoNguoiChoiRepository soNguoiChoiRepository;

    public SoNguoiChoiService(SoNguoiChoiRepository soNguoiChoiRepository) {
        this.soNguoiChoiRepository = soNguoiChoiRepository;
    }

    public SoNguoiChoi getSoNguoiChoiById(Long id) {
        return soNguoiChoiRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy số người chơi với id: " + id));
    }
    public List<SoNguoiChoi> getSoNguoiChoiByPlayerId(Long playerId) {
        List<SoNguoiChoi> list = soNguoiChoiRepository.findByPlayerId(playerId);
        if (list.isEmpty()) {
            throw new RuntimeException("Không tìm thấy số người chơi với playerId: " + playerId);
        }
        return list;
    }

}
