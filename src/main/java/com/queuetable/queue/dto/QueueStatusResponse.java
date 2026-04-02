package com.queuetable.queue.dto;

public record QueueStatusResponse(
        int waitingCount,
        int estimatedWaitMinutes,
        boolean queueOpen
) {}
