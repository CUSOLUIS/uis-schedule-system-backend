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
@Table(name = "faculty")
public class FacultyEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "faculty_id", columnDefinition = "uuid")
	private UUID facultyId;

	@Column(length = 100)
	private String name;
}
