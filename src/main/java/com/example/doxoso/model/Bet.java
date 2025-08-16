package com.example.doxoso.model;


import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "bets")
public class Bet {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        private String mien;
        private String dai;
        private String cachDanh;
        private String soDanh;
        private String soTien;

        @Column(nullable = false)
        private LocalDate ngay = LocalDate.now();

        @ManyToOne
        @JoinColumn(name = "player_id", nullable = false)
        private Player player;
}
