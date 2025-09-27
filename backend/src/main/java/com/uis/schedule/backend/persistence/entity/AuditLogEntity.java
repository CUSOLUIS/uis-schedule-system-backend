package com.uis.schedule.backend.persistence.entity;


import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "audit_log")
public class AuditLogEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "audit_id")
    private Long auditId;

	@Column(length = 256)
	private String action;

	@Column(length = 256)
	private String module;

	@Column(length = 256, name = "origin_ip")
	private String originIp;

	@Column(name = "start_date")
	private LocalDateTime date;

	@OneToOne
	@JoinColumn(name = "user_id")
	private UserEntity userId;
}

