package id.ac.ui.cs.advprog.jsonbackend.common.config;

import id.ac.ui.cs.advprog.jsonbackend.features.authprofile.model.User;
import id.ac.ui.cs.advprog.jsonbackend.features.authprofile.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserRepository userRepository;

    @Value("${app.debug.verbose:false}")
    private boolean verboseLogging;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String username;

        if (verboseLogging) {
            log.info("[DEBUG] Request URL: {} {}", request.getMethod(), request.getRequestURI());
            log.info("[DEBUG] Authorization Header: {}", authHeader != null ? "Present" : "Missing");
        }

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            if (authHeader != null && verboseLogging) {
                log.warn("[DEBUG] Authorization header does not start with Bearer ");
            }
            filterChain.doFilter(request, response);
            return;
        }

        jwt = authHeader.substring(7);
        try {
            username = jwtService.extractUsername(jwt);
            if (verboseLogging) log.info("[DEBUG] Extracted Username: {}", username);

            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                String role = jwtService.extractClaim(jwt, claims -> claims.get("role", String.class));
                if (verboseLogging) log.info("[DEBUG] Extracted Role: {}", role);

                User user = userRepository.findByUsername(username).orElse(null);

                if (user != null && jwtService.isTokenValid(jwt, user)) {
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            user,
                            null,
                            Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + role))
                    );
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                    if (verboseLogging) log.info("[DEBUG] Authentication successful for user: {}", username);
                } else {
                    if (verboseLogging) log.warn("[DEBUG] Token invalid for user: {}", username);
                }
            }
        } catch (Throwable t) {
            if (verboseLogging) {
                log.error("[DEBUG] JWT Authentication failed: ", t);
            }
        }

        filterChain.doFilter(request, response);
    }
}