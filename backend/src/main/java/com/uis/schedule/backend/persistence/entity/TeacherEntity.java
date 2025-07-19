package com.uis.schedule.backend.persistence.entity;

import jakarta.persistence.*;
import lombok.*;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "teacher")
public class TeacherEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "teacher_id")
	private Long teacherId;

	@Column(length = 250)
	private String availability;

	@Column(length = 250)
	private String department;

	@ManyToOne
	@JoinColumn(name = "user_id")
	private UserEntity userId;
}
