package com.example.backProcesamientoStream.controller;

import com.example.backProcesamientoStream.impl.GenerarImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;


@RestController
@RequestMapping("/api/cotizaciones")
@RequiredArgsConstructor
public class CotizacionController {

    private final GenerarImpl generarImpl;

    // Propiedades de paginación configurables desde application.properties o variables de entorno
    @Value("${csv.default.page:0}")
    private int defaultPage;

    @Value("${csv.default.size:100}")
    private int defaultSize;

    @Value("${csv.auto.paginate:true}")
    private boolean autoPaginate;

    /**
     * Endpoint para generar manualmente el CSV de cotizaciones.
     * Página, tamaño y autoPaginate se toman desde properties/env.
     *
     * @return URL de S3 o path local donde se guardó el CSV
     */
    @GetMapping("/generar-csv")
    public ResponseEntity<String> generarCsv() {
        try {
            // Llama al backend con los parámetros configurables
            String url = generarImpl.generarCotizacionesCsv(defaultPage, defaultSize);

            String mensaje = "CSV generado correctamente: " + url +
                    " | Página inicial: " + defaultPage +
                    " | Tamaño página: " + defaultSize +
                    " | AutoPaginate: " + autoPaginate;

            return ResponseEntity.ok(mensaje);

        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body("Error generando CSV: " + e.getMessage());
        }
    }

    /**
     * Endpoint opcional para simular la subida local en lugar de S3
     */
    @GetMapping("/generar-csv-local")
    public ResponseEntity<String> generarCsvLocal() {
        try {
            byte[] csvData = generarImpl.generarCotizacionesCsv(defaultPage, defaultSize).getBytes();
            String path = generarImpl.uploadCsvToLocal(csvData, "cotizaciones_local.csv");

            String mensaje = "CSV guardado localmente: " + path +
                    " | Página inicial: " + defaultPage +
                    " | Tamaño página: " + defaultSize +
                    " | AutoPaginate: " + autoPaginate;

            return ResponseEntity.ok(mensaje);

        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body("Error guardando CSV localmente: " + e.getMessage());
        }
    }
}


