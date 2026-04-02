package com.queuetable.table.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateTableRequest(
        @NotBlank @Size(max = 100) String label,
        @Min(1) int capacity,
        @Size(max = 100) String zone
) {}
