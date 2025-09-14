package com.example.doxoso.repository;

import com.example.doxoso.model.SoNguoiChoi;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface SoNguoiChoiRepository extends JpaRepository<SoNguoiChoi, Long> {

        // 2 dòng dưới vốn có sẵn từ JpaRepository, có thể bỏ, nhưng giữ cũng không sao:
        List<SoNguoiChoi> findAll();
        Optional<SoNguoiChoi> findById(Long id);

        // Nếu entity SoNguoiChoi có field playerId (Long)
        List<SoNguoiChoi> findByPlayerId(Long playerId);

        // Hoặc nếu quan hệ @ManyToOne Player -> SoNguoiChoi.player
        List<SoNguoiChoi> findByPlayer_Id(Long playerId);

        // Giảm N+1: eager fetch player khi lấy toàn bộ
        @EntityGraph(attributePaths = "player")
        @Query("select s from SoNguoiChoi s")
        List<SoNguoiChoi> findAllWithPlayer();

        // Lấy theo player trong khoảng ngày (giả sử field là 'ngay' kiểu LocalDate)
        @EntityGraph(attributePaths = "player")
        List<SoNguoiChoi> findByPlayer_IdAndNgayBetween(Long playerId, LocalDate from, LocalDate to);

        // Lấy đúng 1 ngày
        List<SoNguoiChoi> findByPlayerIdAndNgay(Long playerId, LocalDate ngay);
        List<SoNguoiChoi> findByNgay(LocalDate ngay);


        // Tối ưu: lấy trực tiếp danh sách playerId trong ngày (không tải hết sổ)
        @Query("select distinct s.player.id from SoNguoiChoi s where s.ngay = :ngay")
        List<Long> findDistinctPlayerIdsByNgay(@Param("ngay") LocalDate ngay);

        List<SoNguoiChoi> findByPlayer_IdAndNgay(Long playerId, LocalDate ngay);
}
