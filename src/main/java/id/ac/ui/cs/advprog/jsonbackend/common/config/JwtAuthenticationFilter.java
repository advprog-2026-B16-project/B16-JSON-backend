package id.ac.ui.cs.advprog.jsonbackend.common.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    @Value("${app.debug.verbose:false}")
    private boolean verboseLogging;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        final String authHeader = request.getHeader("Authorization");
        
        if (verboseLogging) {
            log.info("[DEBUG] Request URL: {} {}", request.getMethod(), request.getRequestURI());
            log.info("[DEBUG] Authorization Header: {}", authHeader != null ? "Present" : "Missing");
        }

        final String jwt;
        final String username;
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            if (verboseLogging && authHeader != null) {
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
                if (jwtService.isTokenValid(jwt, username)) {
                    String role = jwtService.extractClaim(jwt, claims -> claims.get("role", String.class));
                    if (verboseLogging) log.info("[DEBUG] Extracted Role: {}", role);
                    
                    List<SimpleGrantedAuthority> authorities = Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + role));

                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            username,
                            null,
                            authorities
                    );
                    authToken.setDetails(
                            new WebAuthenticationDetailsSource().buildDetails(request)
                    );
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
