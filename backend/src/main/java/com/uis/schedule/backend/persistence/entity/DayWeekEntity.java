package com.uis.schedule.backend.persistence.entity;


import jakarta.persistence.*;
import lombok.*;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "day_of_week")
public class DayWeekEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "day_id")
	private Long dayId;

	@Column(length=16)
	private String name;
}
