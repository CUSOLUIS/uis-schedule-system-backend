package com.uis.schedule.backend.configuration.jwt;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.uis.schedule.backend.persistence.entity.UserEntity;
import com.uis.schedule.backend.persistence.repository.UserRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class CustomerDetailService implements UserDetailsService {
    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) {
        // Using email as username for authentication
        log.info("Loading user by email: {}", username);
        UserEntity userDetail = userRepository.findUserEntityByEmail(username).orElse(null);
        if (!Objects.isNull(userDetail)) {
            List<SimpleGrantedAuthority> authorityList = new ArrayList<>();

            // Tomamos los roles y los convertimos en un obj que entienda spring security
            userDetail.getRoles()
                    .forEach(role -> authorityList
                            .add(new SimpleGrantedAuthority("ROLE_".concat(role.getRoleEnum().name()))));

            // Tomamos los permisos y los convertimos en un obj que entienda spring security
            userDetail.getRoles().stream()
                    .flatMap(role -> role.getPermissionList().stream())
                    .forEach(permission -> authorityList.add(new SimpleGrantedAuthority(permission.getName())));

            return new org.springframework.security.core.userdetails.User(
                    userDetail.getEmail(),
                    userDetail.getPassword(),
                    userDetail.isEnable(),
                    userDetail.isAccountNoExpired(),
                    userDetail.isCredentialNoExpired(),
                    userDetail.isAccountNoLocked(),
                    authorityList);
        } else {
            throw new UsernameNotFoundException("User not found with email: " + username);
        }
    }
}
