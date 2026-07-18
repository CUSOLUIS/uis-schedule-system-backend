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
@Table(name = "teacher")
public class TeacherEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "teacher_id", columnDefinition = "uuid")
	private UUID teacherId;

	@Column(length = 250)
	private String availability;

	@Column(length = 250)
	private String department;

	@ManyToOne
	@JoinColumn(name = "user_id")
	private UserEntity userId;
}
