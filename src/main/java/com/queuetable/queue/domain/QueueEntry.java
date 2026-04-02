package com.queuetable.queue.domain;

import com.queuetable.shared.audit.AuditableEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "queue_entries")
public class QueueEntry extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "restaurant_id", nullable = false)
    private UUID restaurantId;

    @Column(name = "table_id")
    private UUID tableId;

    @Column(name = "customer_name", nullable = false, length = 200)
    private String customerName;

    @Column(name = "customer_phone", length = 50)
    private String customerPhone;

    @Column(name = "party_size", nullable = false)
    private int partySize;

    @Column(name = "access_token", nullable = false, unique = true)
    private UUID accessToken;

    @Column(nullable = false)
    private int position;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private QueueEntryStatus status = QueueEntryStatus.WAITING;

    @Column(name = "estimated_wait_minutes")
    private Integer estimatedWaitMinutes;

    @Column(name = "notified_at")
    private Instant notifiedAt;

    @Column(name = "is_walk_in", nullable = false)
    private boolean walkIn = false;

    protected QueueEntry() {}

    public QueueEntry(UUID restaurantId, String customerName, int partySize, int position) {
        this.restaurantId = restaurantId;
        this.customerName = customerName;
        this.partySize = partySize;
        this.position = position;
        this.accessToken = UUID.randomUUID();
    }

    public UUID getId() { return id; }
    public UUID getRestaurantId() { return restaurantId; }
    public UUID getTableId() { return tableId; }
    public void setTableId(UUID tableId) { this.tableId = tableId; }
    public String getCustomerName() { return customerName; }
    public String getCustomerPhone() { return customerPhone; }
    public void setCustomerPhone(String customerPhone) { this.customerPhone = customerPhone; }
    public int getPartySize() { return partySize; }
    public UUID getAccessToken() { return accessToken; }
    public int getPosition() { return position; }
    public void setPosition(int position) { this.position = position; }
    public QueueEntryStatus getStatus() { return status; }
    public void setStatus(QueueEntryStatus status) { this.status = status; }
    public Integer getEstimatedWaitMinutes() { return estimatedWaitMinutes; }
    public void setEstimatedWaitMinutes(Integer estimatedWaitMinutes) { this.estimatedWaitMinutes = estimatedWaitMinutes; }
    public Instant getNotifiedAt() { return notifiedAt; }
    public void setNotifiedAt(Instant notifiedAt) { this.notifiedAt = notifiedAt; }
    public boolean isWalkIn() { return walkIn; }
    public void setWalkIn(boolean walkIn) { this.walkIn = walkIn; }
}
