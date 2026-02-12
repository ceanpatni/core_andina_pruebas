package com.example.backProcesamientoStream.impl;

import com.example.backProcesamientoStream.dto.CotizacionDTO;
import com.example.backProcesamientoStream.entity.AuditoriaS3;
import com.example.backProcesamientoStream.repository.AuditoriaS3Repository;
import com.example.backProcesamientoStream.service.S3Service;
import com.example.backProcesamientoStream.util.DBFunctionCaller;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class GenerarImpl {

    private final DBFunctionCaller dbFunctionCaller;
    private final AuditoriaS3Repository auditoriaS3Repository;
    private final S3Service s3Service;

    @Value("${csv.default.page:0}")
    private int defaultPage;

    @Value("${csv.default.size:100}")
    private int defaultSize;

    @Value("${csv.auto.paginate:true}")
    private boolean autoPaginate;

    public String generarCotizacionesCsv(Integer pagina, Integer tamanio) {

        int tm = tamanio != null ? tamanio : defaultSize;
        int pg = pagina != null ? pagina : defaultPage;
        boolean hayDatos = true;

        log.info("=====================================================");
        log.info("INICIO generación CSV cotizaciones");
        log.info("Página inicial: {}", pg);
        log.info("Tamaño página: {}", tm);
        log.info("AutoPaginate: {}", autoPaginate);
        log.info("=====================================================");

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             OutputStreamWriter writer = new OutputStreamWriter(baos, StandardCharsets.UTF_8);
             CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT.withHeader(
                     "id", "estado", "fecha_cotizacion", "fecha_expedicion_poliza",
                     "id_grupo_familiar", "nro_cotizacion", "nro_poliza", "observaciones",
                     "vigencia_cotizacion", "causante_id", "estado_documentacion_id",
                     "tipo_calculo", "consecutivo_cal_cotizacion", "fecha_inicio_vigencia",
                     "causal_id", "causal", "estado_actual", "estado_siguiente",
                     "observacion", "tipo_cambio_automatico", "codigo_estado_causal"
             ))) {

            do {

                log.info("Consultando base de datos → Página: {} | Tamaño: {}", pg, tm);

                List<CotizacionDTO> cotizaciones = dbFunctionCaller.getCotizaciones(pg, tm);

                log.info("Cantidad registros obtenidos: {}", cotizaciones.size());

                if (cotizaciones.isEmpty()) {
                    log.info("No hay más registros para procesar.");
                    hayDatos = false;
                }

                for (CotizacionDTO dto : cotizaciones) {

                    boolean success = false;
                    int attempt = 0;

                    while (!success && attempt < 2) {

                        try {

                            csvPrinter.printRecord(
                                    dto.getId(),
                                    dto.getEstado(),
                                    dto.getFechaCotizacion(),
                                    dto.getFechaExpedicionPoliza(),
                                    dto.getIdGrupoFamiliar(),
                                    dto.getNroCotizacion(),
                                    dto.getNroPoliza(),
                                    dto.getObservaciones(),
                                    dto.getVigenciaCotizacion(),
                                    dto.getCausanteId(),
                                    dto.getEstadoDocumentacionId(),
                                    dto.getTipoCalculo(),
                                    dto.getConsecutivoCalCotizacion(),
                                    dto.getFechaInicioVigencia(),
                                    dto.getCausalId(),
                                    dto.getCausal(),
                                    dto.getEstadoActual(),
                                    dto.getEstadoSiguiente(),
                                    dto.getObservacion(),
                                    dto.getTipoCambioAutomatico(),
                                    dto.getCodigoEstadoCausal()
                            );

                            success = true;

                        } catch (Exception ex) {

                            attempt++;

                            log.error("Error procesando fila ID={} | Intento={}", dto.getId(), attempt, ex);

                            guardarAuditoria("PROCESAMIENTO",
                                    "Error fila id=" + dto.getId(),
                                    ex.getMessage(),
                                    null);

                            if (attempt >= 2) {
                                log.warn("Fila ID={} descartada después de 2 intentos", dto.getId());
                                break;
                            }
                        }
                    }
                }

                if (autoPaginate && hayDatos) {
                    pg++;
                    log.debug("Incrementando a siguiente página: {}", pg);
                }

            } while (hayDatos && autoPaginate);

            csvPrinter.flush();
            byte[] csvData = baos.toByteArray();

            log.info("CSV generado correctamente. Tamaño total (bytes): {}", csvData.length);

            String path = uploadCsvToLocal(csvData, "cotizaciones.csv");

            log.info("FIN generación CSV");
            log.info("=====================================================");

            return path;

        } catch (Exception e) {

            log.error("ERROR GENERAL generando CSV", e);

            guardarAuditoria("PROCESAMIENTO",
                    "Error general generando CSV",
                    e.getMessage(),
                    null);

            throw new RuntimeException("Error generando CSV", e);
        }
    }

    public String uploadCsvToLocal(byte[] csvData, String fileName) {

        try {

            log.info("Iniciando guardado local del archivo: {}", fileName);

            String basePath = "C:\\Users\\Cesar Patiño\\Documents\\guardado_informacion\\S3";

            java.nio.file.Path dirPath = java.nio.file.Paths.get(basePath);

            if (!java.nio.file.Files.exists(dirPath)) {
                java.nio.file.Files.createDirectories(dirPath);
                log.info("Directorio creado correctamente: {}", basePath);
            }

            java.nio.file.Path filePath = dirPath.resolve(fileName);

            java.nio.file.Files.write(filePath, csvData);

            log.info("Archivo guardado exitosamente en: {}", filePath.toAbsolutePath());

            return filePath.toString();

        } catch (Exception e) {

            log.error("ERROR guardando archivo local: {}", fileName, e);

            guardarAuditoria("S3_UPLOAD_LOCAL",
                    "Error guardando archivo local: " + fileName,
                    e.getMessage(),
                    fileName);

            throw new RuntimeException("Error guardando archivo local: " + fileName, e);
        }
    }

    private void guardarAuditoria(String tipo, String mensaje, String detalle, String archivo) {

        try {

            auditoriaS3Repository.save(
                    AuditoriaS3.builder()
                            .fechaEvento(LocalDateTime.now())
                            .tipoEvento(tipo)
                            .mensaje(mensaje)
                            .detalleError(detalle)
                            .archivo(archivo)
                            .build()
            );

        } catch (Exception ex) {
            log.error("Error guardando auditoría en base de datos", ex);
        }
    }
}
