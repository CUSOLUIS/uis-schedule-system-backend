package com.uis.schedule.backend.configuration.testing;

import com.uis.schedule.backend.persistence.entity.UserEntity;
import com.uis.schedule.backend.persistence.repository.UserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.FluentQuery;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

/**
 * Configuración de testing para crear mocks de repositorios sin necesidad de base de datos
 * Usado exclusivamente para probar conectividad con Angular
 */
@Configuration
@Profile("testing")
public class TestingConfig {

    @Bean
    public UserRepository userRepository() {
        return new UserRepository() {
            @Override
            public Optional<UserEntity> findUserEntityByEmail(String email) {
                // Mock user para testing Angular connectivity
                if ("admin".equals(email) || "admin@test.com".equals(email)) {
                    UserEntity mockUser = new UserEntity();
                    mockUser.setUserId(1L);
                    mockUser.setEmail("admin@test.com");
                    mockUser.setPassword("$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iIddu9yM5dcxS5JimVSqQfXprnCa"); // password: admin123
                    mockUser.setName("Admin Test");
                    mockUser.setRole("ADMIN");
                    mockUser.setEnable(true);
                    mockUser.setAccountNoExpired(true);
                    mockUser.setAccountNoLocked(true);
                    mockUser.setCredentialNoExpired(true);
                    return Optional.of(mockUser);
                }
                return Optional.empty();
            }

            @Override
            public void flush() {}

            @Override
            public <S extends UserEntity> S saveAndFlush(S entity) { return entity; }

            @Override
            public <S extends UserEntity> List<S> saveAllAndFlush(Iterable<S> entities) { return List.of(); }

            @Override
            public void deleteAllInBatch(Iterable<UserEntity> entities) {}

            @Override
            public void deleteAllByIdInBatch(Iterable<Long> longs) {}

            @Override
            public void deleteAllInBatch() {}

            @Override
            public UserEntity getOne(Long aLong) { return new UserEntity(); }

            @Override
            public UserEntity getById(Long aLong) { return new UserEntity(); }

            @Override
            public UserEntity getReferenceById(Long aLong) { return new UserEntity(); }

            @Override
            public <S extends UserEntity> Optional<S> findOne(Example<S> example) { return Optional.empty(); }

            @Override
            public <S extends UserEntity> List<S> findAll(Example<S> example) { return List.of(); }

            @Override
            public <S extends UserEntity> List<S> findAll(Example<S> example, Sort sort) { return List.of(); }

            @Override
            public <S extends UserEntity> Page<S> findAll(Example<S> example, Pageable pageable) { return Page.empty(); }

            @Override
            public <S extends UserEntity> long count(Example<S> example) { return 0; }

            @Override
            public <S extends UserEntity> boolean exists(Example<S> example) { return false; }

            @Override
            public <S extends UserEntity, R> R findBy(Example<S> example, Function<FluentQuery.FetchableFluentQuery<S>, R> queryFunction) { return null; }

            @Override
            public <S extends UserEntity> S save(S entity) { return entity; }

            @Override
            public <S extends UserEntity> List<S> saveAll(Iterable<S> entities) { return List.of(); }

            @Override
            public Optional<UserEntity> findById(Long aLong) { return Optional.empty(); }

            @Override
            public boolean existsById(Long aLong) { return false; }

            @Override
            public List<UserEntity> findAll() { return List.of(); }

            @Override
            public List<UserEntity> findAllById(Iterable<Long> longs) { return List.of(); }

            @Override
            public long count() { return 0; }

            @Override
            public void deleteById(Long aLong) {}

            @Override
            public void delete(UserEntity entity) {}

            @Override
            public void deleteAllById(Iterable<? extends Long> longs) {}

            @Override
            public void deleteAll(Iterable<? extends UserEntity> entities) {}

            @Override
            public void deleteAll() {}

            @Override
            public List<UserEntity> findAll(Sort sort) { return List.of(); }

            @Override
            public Page<UserEntity> findAll(Pageable pageable) { return Page.empty(); }
        };
    }
}