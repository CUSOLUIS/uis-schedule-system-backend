package com.uis.schedule.backend.persistence.entity;


import jakarta.persistence.*;
import lombok.*;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "classroom")
public class ClassroomEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "classroom_id")
    private Long classroomId;

	private int number;

	@Column(name = "max_capacity")
	private int maxCapacity;

	@Column(length = 256)
	private String building;

	@Column(length = 256)
	private String campus;

	@Column(length = 100)
	private String type;
}
