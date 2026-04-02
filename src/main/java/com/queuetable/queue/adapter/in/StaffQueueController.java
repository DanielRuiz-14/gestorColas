package com.queuetable.queue.adapter.in;

import com.queuetable.queue.domain.QueueEntryStatus;
import com.queuetable.queue.domain.QueueService;
import com.queuetable.queue.dto.QueueEntryResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
public class StaffQueueController {

    private final QueueService queueService;

    public StaffQueueController(QueueService queueService) {
        this.queueService = queueService;
    }

    @GetMapping("/restaurants/{restaurantId}/queue")
    public ResponseEntity<List<QueueEntryResponse>> listQueue(
            @PathVariable UUID restaurantId,
            @RequestParam(required = false) QueueEntryStatus status) {
        return ResponseEntity.ok(queueService.listQueue(restaurantId, status));
    }

    @PostMapping("/restaurants/{restaurantId}/queue/{entryId}/cancel")
    public ResponseEntity<Void> cancelEntry(@PathVariable UUID restaurantId,
                                            @PathVariable UUID entryId) {
        queueService.cancelEntryByStaff(restaurantId, entryId);
        return ResponseEntity.noContent().build();
    }
}
