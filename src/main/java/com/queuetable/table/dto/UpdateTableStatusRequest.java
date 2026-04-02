package com.queuetable.table.dto;

import com.queuetable.table.domain.TableStatus;
import jakarta.validation.constraints.NotNull;

public record UpdateTableStatusRequest(
        @NotNull TableStatus status
) {}
