package id.ac.ui.cs.advprog.jsonbackend.authprofile.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // Permit EVERYTHING for now to unblock testing
                        .anyRequest().permitAll()
                )
                // Disable TRACE method
                .addFilterBefore((request, response, chain) -> {
                    if ("TRACE".equalsIgnoreCase(((jakarta.servlet.http.HttpServletRequest) request).getMethod())) {
                        ((jakarta.servlet.http.HttpServletResponse) response).setStatus(jakarta.servlet.http.HttpServletResponse.SC_METHOD_NOT_ALLOWED);
                        return;
                    }
                    chain.doFilter(request, response);
                }, org.springframework.security.web.access.channel.ChannelProcessingFilter.class);

        return http.build();
    }

}
