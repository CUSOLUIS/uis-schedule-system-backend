package com.uis.schedule.backend.persistence.entity;

import jakarta.persistence.*;
import lombok.*;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "subject")
public class SubjectEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "subject_id")
	private Long subjectId;

	@Column(length = 250)
	private String code;

	@Column(length = 250)
	private String name;

	private int credits;

	private int theory_hours;

	private int practice_hours;

	@ManyToOne
	@JoinColumn(name = "school_id")
	private SchoolEntity schoolId;
}
