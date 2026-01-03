package com.uis.schedule.backend.persistence.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import lombok.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.Set;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "users")
public class UserEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "user_id")
	private Long userId;

	@Email
	@Column(unique = true, nullable = false, length = 250)
	private String email;

	@Column(length = 256, nullable = false)
	private String password;

	@Pattern(regexp = "^[^0-9]*$", message = "First name cannot contain numbers")
	@Column(name = "first_name", length = 128)
	private String firstName;

	@Pattern(regexp = "^[^0-9]*$", message = "Last name cannot contain numbers")
	@Column(name = "last_name", length = 128)
	private String lastName;

	@Column(unique = true, length = 128)
	private String username;

	@Column(name = "last_session")
	private LocalDateTime lastSession;

	// Security fields
	@Column(name = "is_enabled")
	private boolean isEnable;

	@Column(name = "account_no_expired")
	private boolean accountNoExpired;

	@Column(name = "account_no_locked")
	private boolean accountNoLocked;

	@Column(name = "credential_no_expired")
	private boolean credentialNoExpired;

	@ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@JoinTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "role_id"))
	private Set<RoleEntity> roles = new HashSet<>();

	public String getLastSession() {
		return lastSession != null ? lastSession.toString() : null;
	}

	public LocalDateTime getLastSessionDateTime() {
		return lastSession;
	}

	public void setLastSession(String lastSession) {
		this.lastSession = LocalDateTime.parse(lastSession, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
	}
}
