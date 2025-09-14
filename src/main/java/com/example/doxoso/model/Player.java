package com.example.doxoso.model;


import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "players")
public class Player {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        private String name;
        private String phone;
        private Double hoaHong;
        private Double heSoCachDanh;

        // ✅ chỉ dùng để build DTO, không lưu DB
        @Transient
        private List<DoiChieuKetQuaDto> ketQua;

        @OneToMany(mappedBy = "player", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
        private List<SoNguoiChoi> soNguoiChoi;
}

//       orphanRemoval = true