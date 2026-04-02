package com.queuetable.queue.dto;

import java.util.UUID;

public record JoinQueueResponse(
        UUID entryId,
        UUID accessToken,
        int position,
        int estimatedWaitMinutes
) {}
