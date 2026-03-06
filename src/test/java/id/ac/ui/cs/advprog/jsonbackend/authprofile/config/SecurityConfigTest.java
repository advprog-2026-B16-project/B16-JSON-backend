package id.ac.ui.cs.advprog.jsonbackend.authprofile.config;

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
        
        // No header
        when(request.getHeader("Authorization")).thenReturn(null);
        filter.doFilter(request, response, chain);
        verify(chain).doFilter(request, response);
        
        // Invalid header format
        reset(chain);
        when(request.getHeader("Authorization")).thenReturn("Invalid");
        filter.doFilter(request, response, chain);
        verify(chain).doFilter(request, response);
        
        // Valid header, null username
        reset(chain);
        when(request.getHeader("Authorization")).thenReturn("Bearer validtoken");
        when(jwtService.extractUsername("validtoken")).thenReturn(null);
        filter.doFilter(request, response, chain);
        verify(chain).doFilter(request, response);
        
        // Valid header, invalid token (false)
        reset(chain);
        when(request.getHeader("Authorization")).thenReturn("Bearer validtoken");
        when(jwtService.extractUsername("validtoken")).thenReturn("testuser");
        when(jwtService.isTokenValid("validtoken", "testuser")).thenReturn(false);
        filter.doFilter(request, response, chain);
        verify(chain).doFilter(request, response);
        
        // Valid header, valid token
        reset(chain);
        org.springframework.security.core.context.SecurityContextHolder.clearContext();
        when(request.getHeader("Authorization")).thenReturn("Bearer validtoken");
        when(jwtService.extractUsername("validtoken")).thenReturn("testuser");
        when(jwtService.isTokenValid("validtoken", "testuser")).thenReturn(true);
        when(jwtService.extractClaim(anyString(), any())).thenReturn("ADMIN");
        
        filter.doFilter(request, response, chain);
        verify(chain).doFilter(request, response);
        assertNotNull(org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication());
    }
}
