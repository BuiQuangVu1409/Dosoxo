package com.example.doxoso.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.Table;
import lombok.Getter;

import lombok.Setter;

import java.time.LocalDate;
import java.util.List;


@Getter
@Setter
@Table(name = "Doi Chieu Ket Qua")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DoiChieuKetQuaDto {


    private String soDanh;
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

    private List<String> danhSachDai;


    private List<KetQuaTungDai> ketQuaTungDai;


@Getter
@Setter
    public static class KetQuaTungDai {
            private String tenDai;
            private boolean trung;
            private String giai;
            private String soTrung;
            private String lyDo;
        }
    }

