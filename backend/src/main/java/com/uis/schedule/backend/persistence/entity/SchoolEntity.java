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
@Table(name = "school")
public class SchoolEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "school_id", columnDefinition = "uuid")
	private UUID schoolId;

	@Column(length = 100)
	private String name;

	@ManyToOne
	@JoinColumn(name = "faculty_id")
	private FacultyEntity faculty_id;
}
