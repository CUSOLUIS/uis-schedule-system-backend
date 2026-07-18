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
@Table(name = "schedule")
public class ScheduleEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "schedule_id", columnDefinition = "uuid")
	private UUID scheduleId;

	@ManyToOne
	@JoinColumn(name = "class_id")
	private ClassEntity classId;

	@OneToOne
	@JoinColumn(name = "user_id")
	private UserEntity userId;
}
