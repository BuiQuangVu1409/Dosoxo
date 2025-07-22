package com.example.doxoso.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.List;
import java.util.Map;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LichQuayXoSo {
        private String ngay;
        private String thu;
        private Map<String, List<String>> danhSachDai; // key: miền, value: list đài
}
