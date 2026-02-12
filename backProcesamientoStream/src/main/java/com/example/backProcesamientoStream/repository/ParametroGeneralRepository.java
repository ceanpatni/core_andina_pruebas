package com.example.backProcesamientoStream.repository;


import com.example.backProcesamientoStream.entity.ParametroGeneral;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ParametroGeneralRepository extends JpaRepository<ParametroGeneral, Long> {

    Optional<ParametroGeneral> findById(Long id);

    Optional<ParametroGeneral> findByNombreParametro(String nombreParametro);
}
