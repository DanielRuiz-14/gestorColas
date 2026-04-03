package com.queuetable.table.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Datos para crear varias mesas consecutivas")
public record CreateTablesBulkRequest(
        @Schema(description = "Prefijo comun de las mesas", example = "Mesa")
        @NotBlank
        @Size(max = 95)
        String labelPrefix,
        @Schema(description = "Numero inicial incluido", example = "1")
        @Min(1)
        int fromNumber,
        @Schema(description = "Numero final incluido", example = "20")
        @Min(1)
        int toNumber,
        @Schema(description = "Capacidad de comensales para todas las mesas", example = "4")
        @Min(1)
        int capacity,
        @Schema(description = "Zona compartida por las mesas", example = "Terraza")
        @Size(max = 100)
        String zone
) {}
