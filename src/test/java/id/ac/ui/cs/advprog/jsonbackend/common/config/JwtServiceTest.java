package id.ac.ui.cs.advprog.jsonbackend.common.config;

import id.ac.ui.cs.advprog.jsonbackend.features.authprofile.model.User;
import id.ac.ui.cs.advprog.jsonbackend.features.authprofile.model.UserRole;
import id.ac.ui.cs.advprog.jsonbackend.features.authprofile.model.UserStatus;
import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JwtServiceTest {

    private JwtService jwtService;
    private User user;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();
        String secret = Base64.getEncoder().encodeToString("12345678901234567890123456789012".getBytes(StandardCharsets.UTF_8));
        ReflectionTestUtils.setField(jwtService, "secretKey", secret);
        ReflectionTestUtils.setField(jwtService, "jwtExpiration", 3600000L);
        ReflectionTestUtils.setField(jwtService, "verboseLogging", true);
        user = User.builder()
                .id(UUID.randomUUID())
                .username("titiper")
                .email("titiper@example.com")
                .password("secret")
                .role(UserRole.TITIPER)
                .status(UserStatus.ACTIVE)
                .build();
    }

    @Test
    void generateTokenShouldContainUsernameRoleAndAudience() {
        String token = jwtService.generateToken(user);

        assertEquals("titiper", jwtService.extractUsername(token));
        assertEquals(UserRole.TITIPER.name(), jwtService.extractClaim(token, claims -> claims.get("role", String.class)));
        assertTrue(jwtService.extractClaim(token, Claims::getAudience).contains("json-backend-client"));
        assertTrue(jwtService.isTokenValid(token, user));
    }

    @Test
    void isTokenValidShouldRejectWrongUserAndMalformedToken() {
        String token = jwtService.generateToken(user);
        User otherUser = User.builder()
                .id(UUID.randomUUID())
                .username("other")
                .email("other@example.com")
                .password("secret")
                .role(UserRole.TITIPER)
                .status(UserStatus.ACTIVE)
                .build();

        assertFalse(jwtService.isTokenValid(token, otherUser));
        assertFalse(jwtService.isTokenValid("not-a-token", user));
    }

    @Test
    void isTokenValidShouldRejectExpiredToken() {
        ReflectionTestUtils.setField(jwtService, "jwtExpiration", -1000L);
        String token = jwtService.generateToken(user);

        assertFalse(jwtService.isTokenValid(token, user));
    }
}
