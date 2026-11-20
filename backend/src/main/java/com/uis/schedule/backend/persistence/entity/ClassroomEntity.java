package com.uis.schedule.backend.persistence.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "classroom")
public class ClassroomEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "classroom_id", columnDefinition = "uuid")
    private UUID classroomId;

    @Column(name = "number", nullable = false)
    private String number;

    @Column(name = "max_capacity")
    private Integer maxCapacity;

    @ManyToOne
    @JoinColumn(name = "building_id")
    private BuildingEntity buildingId;

    /** Legacy free-text building name; prefer {@link #buildingId}. */
    @Column(length = 256)
    private String building;

    @Column(length = 256)
    private String campus;

    @Column(length = 100)
    private String type;

    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private boolean isActive = true;
}
