package com.uis.schedule.backend.persistence.entity;

import jakarta.persistence.*;
import lombok.*;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "class")
public class ClassEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "class_id")
	private Long classId;

	@Column(length = 128, name = "class_type")
	private String classType;

	@ManyToOne
	@JoinColumn(name = "user_id")
	private UserEntity userId;

	@ManyToOne
	@JoinColumn(name = "group_id")
	private GroupEntity groupId;
}
