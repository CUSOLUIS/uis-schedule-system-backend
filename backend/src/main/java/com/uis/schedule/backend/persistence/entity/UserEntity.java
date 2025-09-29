package com.uis.schedule.backend.persistence.entity;


import jakarta.persistence.*;
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

	@Column(unique = true, nullable = false, length = 250)
	private String email;

	@Column(length = 256, nullable = false)
	private String password;

	@Column(length = 256)
	private String name;

	@Column(length = 64)
	private String role;

	private String permissions;

	@Column(name = "last_session")
	private LocalDateTime lastSession;

	//Security fields
	@Column(name = "is_enabled")
	private boolean isEnable;

	@Column(name = "account_no_expired")
	private boolean accountNoExpired;

	@Column(name = "account_no_locked")
	private boolean accountNoLocked;

	@Column(name = "credential_no_expired")
	private boolean credentialNoExpired;

	@ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@JoinTable(name="user_roles", joinColumns = @JoinColumn(name="user_id"),inverseJoinColumns = @JoinColumn(name="role_id"))
	private Set<RoleEntity> roles = new HashSet<>();

	public String getLastSession(){
		return lastSession.toString();
	}

	public void setLastSession(String lastSession){
		this.lastSession = LocalDateTime.parse(lastSession, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
	}
}

