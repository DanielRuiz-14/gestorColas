package com.queuetable.queue.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record JoinQueueRequest(
        @NotBlank @Size(max = 200) String customerName,
        @Min(1) int partySize,
        @Size(max = 50) String customerPhone
) {}
