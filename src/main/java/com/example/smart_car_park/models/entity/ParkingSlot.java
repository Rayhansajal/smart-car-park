package com.example.smart_car_park.models.entity;

import com.example.smart_car_park.models.enums.SlotStatus;
import com.example.smart_car_park.models.enums.VehicleType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "parking_slots",
        uniqueConstraints = @UniqueConstraint(columnNames = {"lot_id", "slot_no"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ParkingSlot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lot_id", nullable = false)
    private ParkingLot lot;

    @Column(name = "slot_no", nullable = false, length = 20)
    private String slotNo;

    @Column(nullable = false)
    @Builder.Default
    private Integer floor = 1;

    @Column(length = 10)
    private String zone;

    @Enumerated(EnumType.STRING)
    @Column(name = "slot_type", nullable = false, length = 30)
    private VehicleType slotType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    @Builder.Default
    private SlotStatus status = SlotStatus.AVAILABLE;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
