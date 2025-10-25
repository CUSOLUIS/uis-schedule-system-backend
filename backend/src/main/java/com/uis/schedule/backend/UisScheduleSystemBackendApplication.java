package com.uis.schedule.backend;

import com.uis.schedule.backend.persistence.entity.PermissionEntity;
import com.uis.schedule.backend.persistence.entity.RoleEntity;
import com.uis.schedule.backend.persistence.entity.RoleEnum;
import com.uis.schedule.backend.persistence.entity.UserEntity;
import com.uis.schedule.backend.persistence.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.List;
import java.util.Set;

@SpringBootApplication
public class UisScheduleSystemBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(UisScheduleSystemBackendApplication.class, args);
	}

	@Bean
	CommandLineRunner init(UserRepository userRepository) {
		return args -> {
			// Encoder para encriptar las contraseñas
			org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder passwordEncoder = new org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder();

			PermissionEntity createEditSchedulePermission = PermissionEntity.builder()
					.name("CREATE_EDIT_SCHEDULE")
					.build();

			PermissionEntity approvePublishSchedulePermission = PermissionEntity.builder()
					.name("APPROVE_PUBLISH_SCHEDULE")
					.build();

			PermissionEntity changeRequestPermission = PermissionEntity.builder()
					.name("CHANGE_REQUEST")
					.build();

			PermissionEntity viewSchedulePermission = PermissionEntity.builder()
					.name("VIEW_SCHEDULE")
					.build();

			PermissionEntity editUsersPermission = PermissionEntity.builder()
					.name("EDIT_USERS")
					.build();

			RoleEntity administradorRole = RoleEntity.builder()
					.roleEnum(RoleEnum.ADMINISTRADOR)
					.permissionList(Set.of(createEditSchedulePermission, approvePublishSchedulePermission,
							changeRequestPermission, viewSchedulePermission, editUsersPermission))
					.build();

			RoleEntity operadorRole = RoleEntity.builder()
					.roleEnum(RoleEnum.OPERADOR)
					.permissionList(
							Set.of(approvePublishSchedulePermission, changeRequestPermission, viewSchedulePermission))
					.build();

			RoleEntity docenteRole = RoleEntity.builder()
					.roleEnum(RoleEnum.DOCENTE)
					.permissionList(Set.of(changeRequestPermission, viewSchedulePermission))
					.build();

			RoleEntity estudianteRole = RoleEntity.builder()
					.roleEnum(RoleEnum.ESTUDIANTE)
					.permissionList(Set.of(viewSchedulePermission))
					.build();

			UserEntity nicoleUser = UserEntity.builder()
					.email("nicole0202")
					.password(passwordEncoder.encode("1234"))
					.isEnable(true)
					.accountNoExpired(true)
					.accountNoLocked(true)
					.credentialNoExpired(true)
					.roles(Set.of(administradorRole))
					.build();

			UserEntity marcosUser = UserEntity.builder()
					.email("Marcos123")
					.password(passwordEncoder.encode("1234"))
					.isEnable(true)
					.accountNoExpired(true)
					.accountNoLocked(true)
					.credentialNoExpired(true)
					.roles(Set.of(estudianteRole))
					.build();

			UserEntity dayannaUser = UserEntity.builder()
					.email("Dayanna123")
					.password(passwordEncoder.encode("1234"))
					.isEnable(true)
					.accountNoExpired(true)
					.accountNoLocked(true)
					.credentialNoExpired(true)
					.roles(Set.of(operadorRole))
					.build();

			UserEntity julianUser = UserEntity.builder()
					.email("Julian123")
					.password(passwordEncoder.encode("1234"))
					.isEnable(true)
					.accountNoExpired(true)
					.accountNoLocked(true)
					.credentialNoExpired(true)
					.roles(Set.of(docenteRole))
					.build();

			userRepository.saveAll(List.of(julianUser, nicoleUser, dayannaUser, marcosUser));
		};
	}
}
