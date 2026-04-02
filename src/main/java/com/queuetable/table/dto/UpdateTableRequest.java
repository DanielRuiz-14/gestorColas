package com.queuetable.table.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

public record UpdateTableRequest(
        @Size(max = 100) String label,
        @Min(1) Integer capacity,
        @Size(max = 100) String zone
) {}
