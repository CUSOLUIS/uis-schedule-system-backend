package com.uis.schedule.backend;

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

			RoleEntity administradorRole = RoleEntity.builder()
					.roleEnum(RoleEnum.ADMINISTRADOR)
					.build();

			RoleEntity operadorRole = RoleEntity.builder()
					.roleEnum(RoleEnum.OPERADOR)
					.build();

			RoleEntity docenteRole = RoleEntity.builder()
					.roleEnum(RoleEnum.DOCENTE)
					.build();

			RoleEntity estudianteRole = RoleEntity.builder()
					.roleEnum(RoleEnum.ESTUDIANTE)
					.build();

			UserEntity nicoleUser = UserEntity.builder()
					.firstName("Nicole")
					.lastName("Alvarez")
					.username("nalvarez")
					.email("nicole0202@uis.edu.co")
					.password(passwordEncoder.encode("1234"))
					.isEnable(true)
					.accountNoExpired(true)
					.accountNoLocked(true)
					.credentialNoExpired(true)
					.roles(Set.of(administradorRole))
					.build();

			UserEntity marcosUser = UserEntity.builder()
					.firstName("Marcos")
					.lastName("Perez")
					.username("mperez")
					.email("Marcos123@uis.edu.co")
					.password(passwordEncoder.encode("1234"))
					.isEnable(true)
					.accountNoExpired(true)
					.accountNoLocked(true)
					.credentialNoExpired(true)
					.roles(Set.of(estudianteRole))
					.build();

			UserEntity dayannaUser = UserEntity.builder()
					.firstName("Dayanna")
					.lastName("Diaz")
					.username("ddiaz")
					.email("Dayanna123@uis.edu.co")
					.password(passwordEncoder.encode("1234"))
					.isEnable(true)
					.accountNoExpired(true)
					.accountNoLocked(true)
					.credentialNoExpired(true)
					.roles(Set.of(operadorRole))
					.build();

			UserEntity julianUser = UserEntity.builder()
					.firstName("Julian")
					.lastName("Gomez")
					.username("jgomez")
					.email("Julian123@uis.edu.co")
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
