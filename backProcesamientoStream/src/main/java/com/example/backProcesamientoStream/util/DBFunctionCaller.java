package com.example.backProcesamientoStream.util;

import com.example.backProcesamientoStream.dto.CotizacionDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DBFunctionCaller {

    private final JdbcTemplate jdbcTemplate;

    private final int DEFAULT_PAGINA = 0;
    private final int DEFAULT_TAMANIO = 100;

    public List<CotizacionDTO> getCotizaciones(Integer pagina, Integer tamanio) {
        int pg = pagina != null ? pagina : DEFAULT_PAGINA;
        int tm = tamanio != null ? tamanio : DEFAULT_TAMANIO;

        String sql = "SELECT * FROM obtener_cotizaciones(?, ?)";

        return jdbcTemplate.query(sql, new Object[]{pg, tm}, new CotizacionRowMapper());
    }

    private static class CotizacionRowMapper implements RowMapper<CotizacionDTO> {
        @Override
        public CotizacionDTO mapRow(ResultSet rs, int rowNum) throws SQLException {
            return CotizacionDTO.builder()
                    .id(rs.getLong("id"))
                    .estado(rs.getString("estado"))
                    .fechaCotizacion(rs.getTimestamp("fecha_cotizacion") != null ? rs.getTimestamp("fecha_cotizacion").toLocalDateTime().toLocalDate() : null)
                    .fechaExpedicionPoliza(rs.getTimestamp("fecha_expedicion_poliza") != null ? rs.getTimestamp("fecha_expedicion_poliza").toLocalDateTime().toLocalDate() : null)
                    .idGrupoFamiliar(rs.getLong("id_grupo_familiar"))
                    .nroCotizacion(rs.getString("nro_cotizacion"))
                    .nroPoliza(rs.getString("nro_poliza"))
                    .observaciones(rs.getString("observaciones"))
                    .vigenciaCotizacion(rs.getTimestamp("vigencia_cotizacion") != null ? rs.getTimestamp("vigencia_cotizacion").toLocalDateTime().toLocalDate() : null)
                    .causanteId(rs.getLong("causante_id"))
                    .estadoDocumentacionId(rs.getLong("estado_documentacion_id"))
                    .tipoCalculo(rs.getString("tipo_calculo"))
                    .consecutivoCalCotizacion(rs.getInt("consecutivo_cal_cotizacion"))
                    .fechaInicioVigencia(rs.getTimestamp("fecha_inicio_vigencia") != null ? rs.getTimestamp("fecha_inicio_vigencia").toLocalDateTime().toLocalDate() : null)
                    .causalId(rs.getLong("causal_id"))
                    .causal(rs.getString("causal"))
                    .estadoActual(rs.getString("estado_actual"))
                    .estadoSiguiente(rs.getString("estado_siguiente"))
                    .observacion(rs.getString("observacion"))
                    .tipoCambioAutomatico(rs.getBoolean("tipo_cambio_automatico")) // boolean directo
                    .codigoEstadoCausal(rs.getString("codigo_estado_causal"))
                    .build();
        }
    }
}
