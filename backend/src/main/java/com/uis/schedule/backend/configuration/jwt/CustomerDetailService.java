package com.uis.schedule.backend.configuration.jwt;

import java.util.Collections;
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
        if(!Objects.isNull(userDetail)) {
            return new org.springframework.security.core.userdetails.User(
                userDetail.getEmail(),
                userDetail.getPassword(),
                Collections.singletonList(new SimpleGrantedAuthority(userDetail.getRole()))
            );
        } else {
            throw new UsernameNotFoundException("User not found with email: " + username);
        }
    }
}
