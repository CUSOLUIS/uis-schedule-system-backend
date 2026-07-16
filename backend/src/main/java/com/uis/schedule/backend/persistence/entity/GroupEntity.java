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
@Table(name = "groups")
public class GroupEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "group_id", columnDefinition = "uuid")
    private UUID groupId;

    @Column(length = 250, name = "group_name")
    private String name;

    @Column(name = "capacity", nullable = false)
    private Integer capacity;

    @ManyToOne
    @JoinColumn(name = "teacher_id")
    private TeacherEntity teacherId;

    @ManyToOne
    @JoinColumn(name = "period_id")
    private AcademicPeriodEntity periodId;

    @ManyToOne
    @JoinColumn(name = "subject_id")
    private SubjectEntity subjectId;

    @ManyToOne
    @JoinColumn(name = "classroom_id")
    private ClassroomEntity classroomId;

    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private boolean isActive = true;
}
