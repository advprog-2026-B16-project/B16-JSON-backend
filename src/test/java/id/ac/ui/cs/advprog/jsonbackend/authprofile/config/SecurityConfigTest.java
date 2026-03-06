package id.ac.ui.cs.advprog.jsonbackend.authprofile.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.Filter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
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
        SecurityConfig config = new SecurityConfig();
        assertTrue(config.passwordEncoder() instanceof BCryptPasswordEncoder);
    }

    @Test
    void testSecurityFilterChainConfiguration() throws Exception {
        SecurityConfig config = new SecurityConfig();
        HttpSecurity http = mock(HttpSecurity.class);
        
        // Mock all the fluent API calls
        when(http.csrf(any())).thenReturn(http);
        when(http.sessionManagement(any())).thenReturn(http);
        when(http.authorizeHttpRequests(any())).thenReturn(http);
        when(http.addFilterBefore(any(), any())).thenReturn(http);
        
        SecurityFilterChain chain = config.securityFilterChain(http);
        
        // build() usually returns null if not mocked, which is fine for coverage
        verify(http).addFilterBefore(any(Filter.class), eq(ChannelProcessingFilter.class));
    }

    @Test
    void testTraceFilterLogic() throws Exception {
        SecurityConfig config = new SecurityConfig();
        HttpSecurity http = mock(HttpSecurity.class);
        
        // Mock the fluent API
        when(http.csrf(any())).thenReturn(http);
        when(http.sessionManagement(any())).thenReturn(http);
        when(http.authorizeHttpRequests(any())).thenReturn(http);
        when(http.addFilterBefore(any(), any())).thenReturn(http);
        
        ArgumentCaptor<Filter> filterCaptor = ArgumentCaptor.forClass(Filter.class);
        config.securityFilterChain(http);
        verify(http).addFilterBefore(filterCaptor.capture(), eq(ChannelProcessingFilter.class));
        
        Filter traceFilter = filterCaptor.getValue();
        
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        FilterChain chain = mock(FilterChain.class);
        
        // Test TRACE method blocked
        when(request.getMethod()).thenReturn("TRACE");
        traceFilter.doFilter(request, response, chain);
        verify(response).setStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
        verify(chain, never()).doFilter(any(), any());
        
        // Test non-TRACE method allowed
        reset(response, chain);
        when(request.getMethod()).thenReturn("GET");
        traceFilter.doFilter(request, response, chain);
        verify(chain).doFilter(request, response);
        verify(response, never()).setStatus(anyInt());
    }
}
