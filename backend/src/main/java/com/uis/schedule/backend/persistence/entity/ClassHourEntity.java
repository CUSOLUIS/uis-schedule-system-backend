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

	private LocalTime hour;

	@ManyToOne
	@JoinColumn(name = "group_id")
	private GroupEntity groupId;

	@ManyToOne
	@JoinColumn(name = "classroom_id")
	private ClassroomEntity classroomId;
}
