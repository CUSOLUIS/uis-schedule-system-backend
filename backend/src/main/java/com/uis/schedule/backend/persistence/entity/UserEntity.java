package com.uis.schedule.backend.persistence.entity;


import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

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

	@Column(nullable = false)
	private boolean active;

	@Column(name = "last_session")
	private LocalDateTime lastSession;
	public String getLastSession(){
		return lastSession.toString();
	}

	public void setLastSession(String lastSession){
		this.lastSession = LocalDateTime.parse(lastSession, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
	}
}

