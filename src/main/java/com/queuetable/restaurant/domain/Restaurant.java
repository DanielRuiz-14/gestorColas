package com.queuetable.restaurant.domain;

import com.queuetable.shared.audit.AuditableEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.Map;
import java.util.UUID;

@Entity
@Table(name = "restaurants")
public class Restaurant extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true, length = 100)
    private String slug;

    @Column(nullable = false, length = 500)
    private String address;

    @Column(length = 50)
    private String phone;

    private String description;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "opening_hours", nullable = false, columnDefinition = "jsonb")
    private Map<String, Object> openingHours = Map.of();

    @Column(name = "qr_code_url", length = 500)
    private String qrCodeUrl;

    @Column(name = "is_active", nullable = false)
    private boolean active = true;

    protected Restaurant() {}

    public Restaurant(String name, String slug, String address) {
        this.name = name;
        this.slug = slug;
        this.address = address;
    }

    public UUID getId() { return id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getSlug() { return slug; }
    public void setSlug(String slug) { this.slug = slug; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public Map<String, Object> getOpeningHours() { return openingHours; }
    public void setOpeningHours(Map<String, Object> openingHours) { this.openingHours = openingHours; }
    public String getQrCodeUrl() { return qrCodeUrl; }
    public void setQrCodeUrl(String qrCodeUrl) { this.qrCodeUrl = qrCodeUrl; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
}
