package com.example.backProcesamientoStream.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CotizacionDTO {

    private Long id;
    private String estado;
    private LocalDate fechaCotizacion;
    private LocalDate fechaExpedicionPoliza;
    private Long idGrupoFamiliar;
    private String nroCotizacion;
    private String nroPoliza;
    private String observaciones;
    private LocalDate vigenciaCotizacion;
    private Long causanteId;
    private Long estadoDocumentacionId;
    private String tipoCalculo;
    private Integer consecutivoCalCotizacion;
    private LocalDate fechaInicioVigencia;
    private Long causalId;
    private String causal;
    private String estadoActual;
    private String estadoSiguiente;
    private String observacion;
    private Boolean tipoCambioAutomatico; // <-- ahora es Boolean
    private String codigoEstadoCausal;
}
