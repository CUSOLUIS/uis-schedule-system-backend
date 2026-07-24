package com.uis.schedule.backend.configuration.jwt;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import com.uis.schedule.backend.persistence.repository.RevokedTokenRepository ;

@Slf4j
@Component
public class JwtFilter extends OncePerRequestFilter {
  private static final String AUTHORIZATION_HEADER = "Authorization";
  private static final String BEARER_PREFIX = "Bearer ";

  @Autowired
  private JwtUtil jwtUtil;

  @Autowired
  private CustomerDetailService customerDetailService;

  @Autowired
  private RevokedTokenRepository revokedTokenRepository;

  @Override
  protected boolean shouldNotFilter(HttpServletRequest request) {
    String p = request.getServletPath();
    if (p.equals("/auth/password/change") || p.equals("/auth/logout")) {
      return false;
    }
    return p.startsWith("/auth/")
        || p.startsWith("/swagger-ui/")
        || p.startsWith("/v3/api-docs")
        || p.startsWith("/swagger-resources/")
        || p.startsWith("/webjars/");
  }

  @Override
  protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response,
      @NonNull FilterChain filterChain)
      throws ServletException, IOException {
    String authorizationHeader = request.getHeader(AUTHORIZATION_HEADER);
    String token = null;
    String username = null;

    if (authorizationHeader != null && authorizationHeader.startsWith(BEARER_PREFIX)) {
      token = authorizationHeader.substring(BEARER_PREFIX.length());
      try {
        username = jwtUtil.extractUserName(token);

        String tokenType = jwtUtil.extractTokenType(token);
        if (tokenType != null && !JwtUtil.TOKEN_TYPE_ACCESS.equals(tokenType)) {
          log.warn("Rejected request: token type '{}' is not a valid access token", tokenType);
          response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
          response.setContentType("application/json");
          response.getWriter().write("{\"token\": null, \"message\": \"Invalid token type\"}");
          return;
        }

        String jti = jwtUtil.extractJti(token);
        if (jti != null && revokedTokenRepository.existsByJti(jti)) {
          log.warn("Rejected request with revoked token (jti={})", jti);
          response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
          response.setContentType("application/json");
          response.getWriter().write("{\"token\": null, \"message\": \"Token has been revoked\"}");
          return;
        }

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
          Claims claims = jwtUtil.extractAllClaims(token);
          Object roleClaim = claims.get("roles");

          // Crear lista de autoridades
          List<SimpleGrantedAuthority> authorities = Collections.emptyList();
          if (roleClaim instanceof List<?> roles) {
            authorities = roles.stream()
                .filter(String.class::isInstance)
                .map(r -> new SimpleGrantedAuthority("ROLE_" + (String) r))
                .collect(Collectors.toList());
          }

          UserDetails userDetails = customerDetailService.loadUserByUsername(username);
          if (jwtUtil.validateToken(token, userDetails)) {
            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                userDetails, null, authorities);
            authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authToken);
          }
        }
      } catch (Exception e) {
        log.error("Authentication error: {}", e.getMessage());
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType("application/json");
        response.getWriter().write("{\"token\": null, \"message\": \"Authentication failed: " + e.getMessage() + "\"}");
        return;
      }
    }
    filterChain.doFilter(request, response);
  }

  public String getUsernameFromToken(HttpServletRequest request) {
    String authorizationHeader = request.getHeader(AUTHORIZATION_HEADER);
    String token = null;
    if (authorizationHeader != null && authorizationHeader.startsWith(BEARER_PREFIX)) {
      token = authorizationHeader.substring(BEARER_PREFIX.length());
      return jwtUtil.extractUserName(token);
    }
    return null;
  }

  public Claims getClaimsFromToken(HttpServletRequest request) {
    String authorizationHeader = request.getHeader(AUTHORIZATION_HEADER);
    String token = null;
    if (authorizationHeader != null && authorizationHeader.startsWith(BEARER_PREFIX)) {
      token = authorizationHeader.substring(BEARER_PREFIX.length());
      return jwtUtil.extractAllClaims(token);
    }
    return null;
  }
}
