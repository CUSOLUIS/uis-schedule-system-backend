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
@Table(name = "day_of_week")
public class DayWeekEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "day_id", columnDefinition = "uuid")
	private UUID dayId;

	@Column(length=16)
	private String name;
}
