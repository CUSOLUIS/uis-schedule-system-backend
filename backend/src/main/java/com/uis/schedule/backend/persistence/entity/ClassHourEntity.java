package com.uis.schedule.backend.persistence.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "class_hour")
public class ClassHourEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "class_hour_id", columnDefinition = "uuid")
    private UUID classHourId;

    @ManyToMany
    @JoinTable(
            name = "class_hour_day",
            joinColumns = @JoinColumn(name = "class_hour_id"),
            inverseJoinColumns = @JoinColumn(name = "day_id")
    )
    @Builder.Default
    private Set<DayWeekEntity> days = new HashSet<>();

    @Column(name = "start_time", nullable = false)
    private LocalTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalTime endTime;

    @ManyToOne
    @JoinColumn(name = "group_id")
    private GroupEntity groupId;

    @ManyToOne
    @JoinColumn(name = "classroom_id")
    private ClassroomEntity classroomId;

    @Column(name = "start_date", nullable = false)
    @Builder.Default
    private LocalDate startDate = LocalDate.now();

    @Column(name = "end_date", nullable = false)
    @Builder.Default
    private LocalDate endDate = LocalDate.now().plusMonths(6);

    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private boolean isActive = true;
}
