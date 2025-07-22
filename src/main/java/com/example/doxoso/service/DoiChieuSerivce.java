package com.example.doxoso.service;
import com.example.doxoso.model.DoiChieuKetQuaDto;
import com.example.doxoso.model.SoNguoiChoi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DoiChieuSerivce {
        @Autowired
        private ChuyenDoiNgayService chuyenDoiNgayService;

        public DoiChieuKetQuaDto taoDto(SoNguoiChoi so) {
            DoiChieuKetQuaDto dto = new DoiChieuKetQuaDto();
            dto.setSoDanh(so.getSoDanh());
            dto.setTenDai(so.getTenDai());
            dto.setMien(so.getMien());
            dto.setNgay(so.getNgay());
            dto.setThu(chuyenDoiNgayService.chuyenDoiThu(so.getNgay()));
            dto.setCachDanh(so.getCachDanh());
            dto.setTienDanh(so.getTienDanh());
            return dto;
        }
    }


