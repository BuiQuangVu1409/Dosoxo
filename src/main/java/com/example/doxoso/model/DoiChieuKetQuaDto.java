package com.example.doxoso.model;

import jakarta.persistence.Table;
import lombok.Getter;

import lombok.Setter;

import java.time.LocalDate;
import java.util.List;


@Getter
@Setter
@Table(name = "Doi Chieu Ket Qua")
public class DoiChieuKetQuaDto {


    private String soDanh;
//    private String giai;
    private String tenDai;
    private String mien;
    private LocalDate ngay;
    private String thu;
    private String cachDanh;
    private String tienDanh;

    private boolean trung;
    private String giaiTrung;
    private String cachTrung;
    private List<String> saiLyDo;
    private Double tienTrungBaoLo;
    private Double tienTrungThuong;
    private Double tienTrungDacBiet;

    private double tienTrung;

    private List<String> danhSachDai ;
}
