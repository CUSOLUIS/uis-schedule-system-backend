package com.uis.schedule.backend.persistence.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalTime;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "class_hour")
public class ClassHourEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "class_hour_id")
    private Long classHourId;

    @ManyToOne
    @JoinColumn(name = "day_id")
    private DayWeekEntity day;

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

    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private boolean isActive = true;
}
