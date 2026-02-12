package com.example.backProcesamientoStream.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "auditoria_s3")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuditoriaS3 {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "fecha_evento", nullable = false)
    private LocalDateTime fechaEvento;

    @Column(name = "tipo_evento", nullable = false)
    private String tipoEvento; // "PROCESAMIENTO" o "S3_UPLOAD"

    @Column(name = "mensaje", columnDefinition = "TEXT")
    private String mensaje;

    @Column(name = "detalle_error", columnDefinition = "TEXT")
    private String detalleError;

    @Column(name = "archivo", length = 255)
    private String archivo; // nombre del CSV o identificador en S3
}
