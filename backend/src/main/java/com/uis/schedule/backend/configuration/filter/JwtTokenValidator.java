package com.uis.schedule.backend.configuration.filter;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.uis.schedule.backend.util.mapper.JwtUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collection;

public class JwtTokenValidator extends OncePerRequestFilter {

    private JwtUtils jwtUtils;

    public JwtTokenValidator(JwtUtils jwtUtils) {
        this.jwtUtils = jwtUtils;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {

        // Excluir rutas públicas del filtro JWT
        String requestPath = request.getRequestURI();
        if (requestPath.equals("/auth/login") ||
                requestPath.startsWith("/v3/api-docs") ||
                requestPath.startsWith("/swagger-ui")) {
            filterChain.doFilter(request, response);
            return;
        }

        String jwtToken = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (jwtToken != null) {
            jwtToken = jwtToken.substring(7);
            // Ver si el token es válido para conceder acceso
            DecodedJWT decodedJWT = jwtUtils.validateToken(jwtToken);

            String username = jwtUtils.extractUsername(decodedJWT);
            String stringAuthorities = jwtUtils.getSpecificClaim(decodedJWT, "authorities").asString();
            // Permisos separados con coma
            Collection<? extends GrantedAuthority> authorities = AuthorityUtils
                    .commaSeparatedStringToAuthorityList(stringAuthorities);
            SecurityContext context = SecurityContextHolder.getContext();
            Authentication authentication = new UsernamePasswordAuthenticationToken(username, null, authorities);
            context.setAuthentication(authentication);
            SecurityContextHolder.setContext(context);

        }
        filterChain.doFilter(request, response);
    }
}
