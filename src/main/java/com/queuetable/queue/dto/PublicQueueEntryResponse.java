package com.queuetable.queue.dto;

import com.queuetable.queue.domain.QueueEntry;
import com.queuetable.queue.domain.QueueEntryStatus;

import java.time.Instant;
import java.util.UUID;

public record PublicQueueEntryResponse(
        UUID id,
        String customerName,
        int partySize,
        int position,
        QueueEntryStatus status,
        Integer estimatedWaitMinutes,
        Instant createdAt
) {
    public static PublicQueueEntryResponse from(QueueEntry e) {
        return new PublicQueueEntryResponse(
                e.getId(), e.getCustomerName(),
                e.getPartySize(), e.getPosition(),
                e.getStatus(), e.getEstimatedWaitMinutes(),
                e.getCreatedAt()
        );
    }
}
