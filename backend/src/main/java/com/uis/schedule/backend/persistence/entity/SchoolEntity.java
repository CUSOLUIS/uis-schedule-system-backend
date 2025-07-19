package com.uis.schedule.backend.persistence.entity;


import jakarta.persistence.*;
import lombok.*;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "school")
public class SchoolEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "school_id")
	private Long schoolId;

	@Column(length = 100)
	private String name;

	@ManyToOne
	@JoinColumn(name = "faculty_id")
	private FacultyEntity faculty_id;
}
