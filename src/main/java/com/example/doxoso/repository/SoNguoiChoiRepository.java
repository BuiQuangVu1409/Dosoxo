package com.example.doxoso.repository;

import com.example.doxoso.model.SoNguoiChoi;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface SoNguoiChoiRepository extends JpaRepository<SoNguoiChoi, Long> {
    List<SoNguoiChoi> findAll();
}
