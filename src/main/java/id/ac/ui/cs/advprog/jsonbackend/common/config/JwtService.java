package id.ac.ui.cs.advprog.jsonbackend.common.config;

import id.ac.ui.cs.advprog.jsonbackend.features.authprofile.model.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.Set;

@Slf4j
@Service
public class JwtService {

    @Value("${application.security.jwt.secret-key}")
    private String secretKey;

    @Value("${application.security.jwt.expiration}")
    private long jwtExpiration;

    @Value("${app.debug.verbose:false}")
    private boolean verboseLogging;

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
        return generateToken(extraClaims, user);
    }

    public String generateToken(
            Map<String, Object> extraClaims,
            User user
    ) {
        return Jwts
                .builder()
                .setClaims(extraClaims)
                .setSubject(user.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpiration))
                .setAudience("json-backend-client")
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean isTokenValid(String token, User user) {
        try {
            final String username = extractUsername(token);
            final Set<String> audiences = extractClaim(token, Claims::getAudience);
            
            boolean usernameMatches = username.equals(user.getUsername());
            boolean audienceMatches = audiences != null && audiences.contains("json-backend-client");
            boolean isExpired = isTokenExpired(token);

            if (verboseLogging) {
                if (!usernameMatches) log.warn("[DEBUG] Token username mismatch");
                if (!audienceMatches) log.warn("[DEBUG] Token audience mismatch");
                if (isExpired) log.warn("[DEBUG] Token is expired");
            }

            return (usernameMatches && audienceMatches && !isExpired);
        } catch (Exception e) {
            if (verboseLogging) log.error("[DEBUG] Error validating token: ", e);
            return false;
        }
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private Claims extractAllClaims(String token) {
        return Jwts
                .parser()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}