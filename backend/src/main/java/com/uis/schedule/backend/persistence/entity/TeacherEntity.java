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
@Table(name = "teacher")
public class TeacherEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "teacher_id", columnDefinition = "uuid")
    private UUID teacherId;

    @Column(name = "full_name", length = 250, nullable = false)
    private String fullName;

    /**
     * Catalog link type: PLANTA or CATEDRA.
     */
    @Column(name = "link_type", length = 20, nullable = false)
    @Builder.Default
    private String linkType = "CATEDRA";

    @Column(length = 250)
    private String availability;

    @Column(length = 250)
    private String department;

    /**
     * Optional legacy link to a user account. New catalog teachers do not require a user.
     */
    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity userId;
}
