package com.uis.schedule.backend.persistence.entity;

import jakarta.persistence.*;
import lombok.*;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "groups")
public class GroupEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "group_id")
	private Long groupId;

	@Column(length = 250, name = "group_name")
	private String name;

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
}
