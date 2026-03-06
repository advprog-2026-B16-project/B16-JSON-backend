package id.ac.ui.cs.advprog.jsonbackend.authprofile.config;


import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.channel.ChannelProcessingFilter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;

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
                        // Permit public endpoints
                        .requestMatchers("/api/login/**", "/api/register/**", "/api/hello").permitAll()
                        // Rest require authentication
                        .anyRequest().authenticated()
                )
                // Disable TRACE method
                .addFilterBefore((request, response, chain) -> {
                    if ("TRACE".equalsIgnoreCase(((jakarta.servlet.http.HttpServletRequest) request).getMethod())) {
                        ((jakarta.servlet.http.HttpServletResponse) response).setStatus(jakarta.servlet.http.HttpServletResponse.SC_METHOD_NOT_ALLOWED);
                        return;
                    }
                    chain.doFilter(request, response);
                }, ChannelProcessingFilter.class)
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

}
