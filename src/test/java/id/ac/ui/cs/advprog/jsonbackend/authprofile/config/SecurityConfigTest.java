package id.ac.ui.cs.advprog.jsonbackend.authprofile.config;

import id.ac.ui.cs.advprog.jsonbackend.authprofile.model.User;
import id.ac.ui.cs.advprog.jsonbackend.authprofile.model.UserRole;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.Filter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.channel.ChannelProcessingFilter;
import org.mockito.ArgumentCaptor;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Base64;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SecurityConfigTest {

    @Test
    void testPasswordEncoderBean() {
        JwtAuthenticationFilter jwtAuthFilter = mock(JwtAuthenticationFilter.class);
        SecurityConfig config = new SecurityConfig(jwtAuthFilter);
        assertTrue(config.passwordEncoder() instanceof BCryptPasswordEncoder);
    }

    @Test
    void testSecurityFilterChainConfiguration() throws Exception {
        JwtAuthenticationFilter jwtAuthFilter = mock(JwtAuthenticationFilter.class);
        SecurityConfig config = new SecurityConfig(jwtAuthFilter);
        HttpSecurity http = mock(HttpSecurity.class);
        
        when(http.csrf(any())).thenReturn(http);
        when(http.sessionManagement(any())).thenReturn(http);
        when(http.requiresChannel(any())).thenReturn(http);
        when(http.authorizeHttpRequests(any())).thenReturn(http);
        when(http.addFilterBefore(any(), any())).thenReturn(http);
        
        SecurityFilterChain chain = config.securityFilterChain(http);
        
        verify(http, atLeastOnce()).addFilterBefore(any(Filter.class), any());
    }

    @Test
    void testTraceFilterLogic() throws Exception {
        JwtAuthenticationFilter jwtAuthFilter = mock(JwtAuthenticationFilter.class);
        SecurityConfig config = new SecurityConfig(jwtAuthFilter);
        HttpSecurity http = mock(HttpSecurity.class);
        
        when(http.csrf(any())).thenReturn(http);
        when(http.sessionManagement(any())).thenReturn(http);
        when(http.requiresChannel(any())).thenReturn(http);
        when(http.authorizeHttpRequests(any())).thenReturn(http);
        when(http.addFilterBefore(any(), any())).thenReturn(http);
        
        ArgumentCaptor<Filter> filterCaptor = ArgumentCaptor.forClass(Filter.class);
        config.securityFilterChain(http);
        verify(http).addFilterBefore(filterCaptor.capture(), eq(ChannelProcessingFilter.class));
        
        Filter traceFilter = filterCaptor.getValue();
        
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        FilterChain chain = mock(FilterChain.class);
        
        when(request.getMethod()).thenReturn("TRACE");
        traceFilter.doFilter(request, response, chain);
        verify(response).setStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
        
        reset(response, chain);
        when(request.getMethod()).thenReturn("GET");
        traceFilter.doFilter(request, response, chain);
        verify(chain).doFilter(request, response);
    }

    @Test
    void testJwtFilterLogic() throws Exception {
        JwtService jwtService = mock(JwtService.class);
        JwtAuthenticationFilter filter = new JwtAuthenticationFilter(jwtService);
        
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        FilterChain chain = mock(FilterChain.class);
        
        // 1. No header
        when(request.getHeader("Authorization")).thenReturn(null);
        filter.doFilter(request, response, chain);
        verify(chain).doFilter(request, response);
        
        // 2. Invalid header format
        reset(chain);
        when(request.getHeader("Authorization")).thenReturn("Invalid");
        filter.doFilter(request, response, chain);
        verify(chain).doFilter(request, response);
        
        // 3. Valid header, null username
        reset(chain);
        when(request.getHeader("Authorization")).thenReturn("Bearer validtoken");
        when(jwtService.extractUsername("validtoken")).thenReturn(null);
        filter.doFilter(request, response, chain);
        verify(chain).doFilter(request, response);

        // 4. Valid token
        reset(chain);
        org.springframework.security.core.context.SecurityContextHolder.clearContext();
        when(request.getHeader("Authorization")).thenReturn("Bearer validtoken");
        when(jwtService.extractUsername("validtoken")).thenReturn("testuser");
        when(jwtService.isTokenValid("validtoken", "testuser")).thenReturn(true);
        
        // Use thenAnswer to hit the lambda
        when(jwtService.extractClaim(anyString(), any())).thenAnswer(invocation -> {
            Function<Claims, String> resolver = invocation.getArgument(1);
            Claims mockClaims = mock(Claims.class);
            when(mockClaims.get("role", String.class)).thenReturn("ADMIN");
            return resolver.apply(mockClaims);
        });
        
        filter.doFilter(request, response, chain);
        verify(chain).doFilter(request, response);
        assertNotNull(org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication());
        assertEquals("testuser", org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication().getPrincipal());

        // 5. Authentication already present
        reset(chain);
        // Authentication is still in context from previous step
        filter.doFilter(request, response, chain);
        verify(chain).doFilter(request, response);

        // 6. Token invalid
        reset(chain);
        org.springframework.security.core.context.SecurityContextHolder.clearContext();
        when(request.getHeader("Authorization")).thenReturn("Bearer validtoken");
        when(jwtService.extractUsername("validtoken")).thenReturn("testuser");
        when(jwtService.isTokenValid("validtoken", "testuser")).thenReturn(false);
        filter.doFilter(request, response, chain);
        verify(chain).doFilter(request, response);
        assertNull(org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication());

        // 7. Throwable catch block
        reset(chain);
        when(request.getHeader("Authorization")).thenReturn("Bearer validtoken");
        when(jwtService.extractUsername("validtoken")).thenThrow(new Error("Fatal Error"));
        filter.doFilter(request, response, chain);
        verify(chain).doFilter(request, response);
    }
}
