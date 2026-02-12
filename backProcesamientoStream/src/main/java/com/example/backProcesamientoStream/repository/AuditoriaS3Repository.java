package com.example.backProcesamientoStream.repository;


import com.example.backProcesamientoStream.entity.AuditoriaS3;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuditoriaS3Repository extends JpaRepository<AuditoriaS3, Long> {
    // Aquí podrías agregar consultas específicas si se requiere filtrar por tipo de evento o archivo
}
