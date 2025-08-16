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

        @OneToMany(mappedBy = "player", cascade = CascadeType.ALL, orphanRemoval = true)
        private List<Bet> bets;

}
