package com.uis.schedule.backend.persistence.entity;

import jakarta.persistence.*;
import lombok.*;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "schedule")
public class ScheduleEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "schedule_id")
	private Long scheduleId;

	@ManyToOne
	@JoinColumn(name = "class_id")
	private ClassEntity classId;
}
