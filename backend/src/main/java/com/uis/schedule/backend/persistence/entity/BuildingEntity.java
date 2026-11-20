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
@Table(name = "building")
public class BuildingEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "building_id", columnDefinition = "uuid")
    private UUID buildingId;

    @Column(name = "code", nullable = false, length = 20, unique = true)
    private String code;

    @Column(name = "name", nullable = false, length = 256)
    private String name;

    @Column(length = 256)
    private String faculty;

    @Column(name = "preference_order", nullable = false)
    @Builder.Default
    private Integer preferenceOrder = 100;

    @Column(name = "has_ac", nullable = false)
    @Builder.Default
    private boolean hasAc = false;

    @Column(name = "has_computer_lab", nullable = false)
    @Builder.Default
    private boolean hasComputerLab = false;

    @Column(length = 256)
    private String campus;

    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private boolean isActive = true;
}
