package com.uis.schedule.backend.persistence.entity;

import jakarta.persistence.*;
import lombok.*;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "classroom")
public class ClassroomEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "classroom_id")
    private Long classroomId;

    @Column(name = "number", nullable = false)
    private String number;

    @Column(name = "max_capacity", nullable = false)
    private Integer maxCapacity;

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
