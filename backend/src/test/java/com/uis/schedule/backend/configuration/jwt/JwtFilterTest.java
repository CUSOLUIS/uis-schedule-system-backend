package com.uis.schedule.backend.configuration.jwt;

import com.uis.schedule.backend.persistence.repository.RevokedTokenRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.FilterChain;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JwtFilterTest {

    private static final String TOKEN = "access-token";
    private static final String EMAIL = "ana@uis.edu.co";

    @Mock
    private JwtUtil jwtUtil;
    @Mock
    private CustomerDetailService customerDetailService;
    @Mock
    private RevokedTokenRepository revokedTokenRepository;
    @Mock
    private FilterChain chain;

    @InjectMocks
    private JwtFilter jwtFilter;

    private MockHttpServletRequest request;
    private MockHttpServletResponse response;

    @BeforeEach
    void setUp() {
        request = new MockHttpServletRequest("GET", "/api/users");
        request.setServletPath("/api/users");
        request.addHeader("Authorization", "Bearer " + TOKEN);
        response = new MockHttpServletResponse();

        Claims claims = Jwts.claims();
        claims.put("roles", List.of("USER"));
        when(jwtUtil.extractUserName(TOKEN)).thenReturn(EMAIL);
        when(jwtUtil.extractTokenType(TOKEN)).thenReturn(JwtUtil.TOKEN_TYPE_ACCESS);
        when(jwtUtil.extractJti(TOKEN)).thenReturn("jti-1");
        when(jwtUtil.extractAllClaims(TOKEN)).thenReturn(claims);
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void disabledUser_returns401() throws Exception {
        UserDetails disabled = User.withUsername(EMAIL).password("x").disabled(true).roles("USER").build();
        when(customerDetailService.loadUserByUsername(EMAIL)).thenReturn(disabled);

        jwtFilter.doFilter(request, response, chain);

        assertEquals(401, response.getStatus());
        assertTrue(response.getContentAsString().contains("Account is disabled"));
        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(chain, never()).doFilter(any(), any());
    }

    @Test
    void enabledUser_continuesChain() throws Exception {
        UserDetails enabled = User.withUsername(EMAIL).password("x").roles("USER").build();
        when(customerDetailService.loadUserByUsername(EMAIL)).thenReturn(enabled);
        when(jwtUtil.validateToken(TOKEN, enabled)).thenReturn(true);

        jwtFilter.doFilter(request, response, chain);

        verify(chain).doFilter(request, response);
        assertNotNull(SecurityContextHolder.getContext().getAuthentication());
    }
}
