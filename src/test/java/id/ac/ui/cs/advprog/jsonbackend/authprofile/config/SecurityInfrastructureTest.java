package id.ac.ui.cs.advprog.jsonbackend.authprofile.config;

import id.ac.ui.cs.advprog.jsonbackend.authprofile.model.User;
import id.ac.ui.cs.advprog.jsonbackend.authprofile.model.UserRole;
import id.ac.ui.cs.advprog.jsonbackend.authprofile.service.LoginAttemptService;
import io.jsonwebtoken.Jwts;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Base64;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class SecurityInfrastructureTest {

    private JwtService jwtService;
    private LoginAttemptService loginAttemptService;
    private SanitizationService sanitizationService;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();
        // Set a valid Base64 encoded key for testing (at least 32 bytes)
        String testKey = Base64.getEncoder().encodeToString("very-long-secret-key-that-is-at-least-32-chars-long-12345".getBytes());
        ReflectionTestUtils.setField(jwtService, "secretKey", testKey);
        ReflectionTestUtils.setField(jwtService, "jwtExpiration", 1800000L);

        loginAttemptService = new LoginAttemptService();
        sanitizationService = new SanitizationService();
    }

    @Test
    void testJwtService() {
        User user = User.builder().username("testuser").role(UserRole.TITIPER).build();
        String token = jwtService.generateToken(user);
        
        assertNotNull(token);
        assertEquals("testuser", jwtService.extractUsername(token));
        assertTrue(jwtService.isTokenValid(token, "testuser"));
        assertFalse(jwtService.isTokenValid(token, "wronguser"));
        assertFalse(jwtService.isTokenExpired(token));
    }

    @Test
    void testJwtServiceExpired() {
        ReflectionTestUtils.setField(jwtService, "jwtExpiration", -1000L); // Already expired
        User user = User.builder().username("testuser").role(UserRole.TITIPER).build();
        String token = jwtService.generateToken(user);
        
        assertFalse(jwtService.isTokenValid(token, "testuser"));
        assertTrue(jwtService.isTokenExpired(token));
    }

    @Test
    void testJwtServiceInvalidAudience() {
        String testKey = (String) ReflectionTestUtils.getField(jwtService, "secretKey");
        byte[] keyBytes = Base64.getDecoder().decode(testKey);
        javax.crypto.SecretKey key = io.jsonwebtoken.security.Keys.hmacShaKeyFor(keyBytes);
        
        // Wrong audience
        String token = Jwts.builder()
                .subject("testuser")
                .audience().add("wrong-audience").and()
                .expiration(new Date(System.currentTimeMillis() + 10000))
                .signWith(key)
                .compact();
        assertFalse(jwtService.isTokenValid(token, "testuser"));

        // No audience (will throw NoSuchElementException in next())
        String tokenNoAud = Jwts.builder()
                .subject("testuser")
                .expiration(new Date(System.currentTimeMillis() + 10000))
                .signWith(key)
                .compact();
        assertFalse(jwtService.isTokenValid(tokenNoAud, "testuser"));
    }

    @Test
    void testLoginAttemptService() {
        String ip = "1.2.3.4";
        assertFalse(loginAttemptService.isBlocked(ip));
        
        // One failed attempt
        loginAttemptService.loginFailed(ip);
        assertFalse(loginAttemptService.isBlocked(ip));
        
        // Succeed
        loginAttemptService.loginSucceeded(ip);
        assertFalse(loginAttemptService.isBlocked(ip));

        // Block it
        for (int i = 0; i < 5; i++) {
            loginAttemptService.loginFailed(ip);
        }
        assertTrue(loginAttemptService.isBlocked(ip));
        
        // Test block expiration by manually setting time in cache
        Object cache = ReflectionTestUtils.getField(loginAttemptService, "attemptsCache");
        java.util.Map<String, Object> map = (java.util.Map<String, Object>) cache;
        Object cachedValue = map.get(ip);
        ReflectionTestUtils.setField(cachedValue, "lastAttempt", java.time.LocalDateTime.now().minusMinutes(6));
        
        assertFalse(loginAttemptService.isBlocked(ip));
        assertFalse(map.containsKey(ip)); // Should be removed
    }

    @Test
    void testSanitizationService() {
        String input = "<script>alert('xss')</script><b>Hello</b>";
        String clean = sanitizationService.sanitize(input);
        
        assertFalse(clean.contains("<script>"));
        assertTrue(clean.contains("<b>Hello</b>"));
        assertNull(sanitizationService.sanitize(null));
    }
}
