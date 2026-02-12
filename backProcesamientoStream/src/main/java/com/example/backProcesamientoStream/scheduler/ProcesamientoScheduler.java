package com.example.backProcesamientoStream.scheduler;

import com.example.backProcesamientoStream.entity.ParametroGeneral;
import com.example.backProcesamientoStream.impl.GenerarImpl;
import com.example.backProcesamientoStream.repository.ParametroGeneralRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProcesamientoScheduler {

    private final ParametroGeneralRepository parametroGeneralRepository;
    private final GenerarImpl generarImpl;

    // ID del parámetro que controla si el scheduler está activo
    private final Long parametroIdScheduler = 2L; // ejemplo: id del parámetro "scheduler_habilitado"

    // Propiedades para paginación configurables desde application.properties o variables de entorno
     @Value("${csv.default.page:0}")
     private int defaultPage;

     @Value("${csv.default.size:100}")
     private int defaultSize;

     @Value("${csv.auto.paginate:true}")
     private boolean autoPaginate;

     /**
      * Scheduler que se ejecuta el primer día de cada mes a las 00:01 AM.
      * Página, tamaño y autoPaginate se leen desde propiedades/env.
     */
    @Async
    @Scheduled(cron = "0 1 0 1 * ?")
    public void ejecutarGeneracionAutomatica() {
        try {
            // Leer parámetro que indica si el scheduler está activo
            ParametroGeneral parametro = parametroGeneralRepository.findById(parametroIdScheduler)
                    .orElseGet(() -> ParametroGeneral.builder()
                            .id(parametroIdScheduler)
                            .nombreParametro("scheduler_habilitado")
                            .valor("1")
                            .estado("ACTIVO")
                            .descripcion("Parametro por defecto para habilitar scheduler")
                            .build());

            if ("ACTIVO".equalsIgnoreCase(parametro.getEstado())) {

                // Llamar al backend pasándole los parámetros configurables
                String urlS3 = generarImpl.generarCotizacionesCsv(defaultPage, defaultSize);

                System.out.println("Scheduler ejecutado correctamente. URL S3: " + urlS3 +
                        " | Página inicial: " + defaultPage +
                        " | Tamaño página: " + defaultSize +
                        " | AutoPaginate: " + autoPaginate);

            } else {
                System.out.println("Scheduler deshabilitado por parámetro general.");
            }

        } catch (Exception e) {
            System.err.println("Error ejecutando scheduler: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
