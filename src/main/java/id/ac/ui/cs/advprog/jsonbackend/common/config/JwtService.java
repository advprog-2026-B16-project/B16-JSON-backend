package id.ac.ui.cs.advprog.jsonbackend.common.config;

import id.ac.ui.cs.advprog.jsonbackend.features.authprofile.model.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class JwtService {

    @Value("${app.debug.verbose:false}")
    private boolean verboseLogging;

    @Value("${application.security.jwt.secret-key:very-long-secret-key-that-is-at-least-32-chars}")
    private String secretKey;

    @Value("${application.security.jwt.expiration:1800000}")
    private long jwtExpiration;

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public String generateToken(User user) {
        Map<String, Object> extraClaims = new HashMap<>();
        extraClaims.put("role", user.getRole().name());
        return buildToken(extraClaims, user, jwtExpiration);
    }

    private String buildToken(
            Map<String, Object> extraClaims,
            User user,
            long expiration
    ) {
        return Jwts
                .builder()
                .claims(extraClaims)
                .subject(user.getUsername())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .audience().add("json-backend-client").and()
                .signWith(getSignInKey(), Jwts.SIG.HS256)
                .compact();
    }

    public boolean isTokenValid(String token, String username) {
        try {
            final Claims claims = extractAllClaims(token);
            final String extractedUsername = claims.getSubject();
            
            boolean usernameMatches = extractedUsername.equals(username);
            
            String audience = null;
            if (claims.getAudience() != null && !claims.getAudience().isEmpty()) {
                audience = claims.getAudience().iterator().next();
            }
            
            boolean audienceMatches = "json-backend-client".equals(audience);
            
            if (verboseLogging) {
                if (!usernameMatches) log.warn("[DEBUG] Token username mismatch: expected {}, got {}", username, extractedUsername);
                if (!audienceMatches) log.warn("[DEBUG] Token audience mismatch: expected json-backend-client, got {}", audience);
            }
            
            return usernameMatches && audienceMatches;
        } catch (Exception e) {
            if (verboseLogging) log.error("[DEBUG] Error validating token: ", e);
            return false;
        }
    }

    public boolean isTokenExpired(String token) {
        try {
            return extractClaim(token, Claims::getExpiration).before(new Date());
        } catch (Exception e) {
            return true;
        }
    }

    private Claims extractAllClaims(String token) {
        return Jwts
                .parser()
                .verifyWith(getSignInKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private SecretKey getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
