package com.queuetable.queue.adapter.in;

import com.queuetable.queue.domain.QueueService;
import com.queuetable.queue.dto.*;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/public")
public class PublicQueueController {

    private final QueueService queueService;

    public PublicQueueController(QueueService queueService) {
        this.queueService = queueService;
    }

    @GetMapping("/restaurants/{slug}")
    public ResponseEntity<PublicRestaurantResponse> getRestaurantInfo(@PathVariable String slug) {
        return ResponseEntity.ok(queueService.getPublicRestaurantInfo(slug));
    }

    @GetMapping("/restaurants/{slug}/queue/status")
    public ResponseEntity<QueueStatusResponse> getQueueStatus(@PathVariable String slug) {
        return ResponseEntity.ok(queueService.getQueueStatus(slug));
    }

    @PostMapping("/restaurants/{slug}/queue")
    public ResponseEntity<JoinQueueResponse> joinQueue(@PathVariable String slug,
                                                       @Valid @RequestBody JoinQueueRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(queueService.joinQueue(slug, request));
    }

    @GetMapping("/queue/{entryId}")
    public ResponseEntity<PublicQueueEntryResponse> getEntryStatus(
            @PathVariable UUID entryId,
            @RequestParam UUID accessToken) {
        return ResponseEntity.ok(queueService.getEntryStatus(entryId, accessToken));
    }

    @DeleteMapping("/queue/{entryId}")
    public ResponseEntity<Void> cancelEntry(@PathVariable UUID entryId,
                                            @RequestParam UUID accessToken) {
        queueService.cancelEntryByCustomer(entryId, accessToken);
        return ResponseEntity.noContent().build();
    }
}
