package StudentManagementSystem.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import StudentManagementSystem.entity.User;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {

    private final String SECRET = "mysupersecretkeymysupersecretkey12345";
    private final Key key = Keys.hmacShaKeyFor(SECRET.getBytes());

    // Token validity: 24 hours to reduce unexpected logouts during sessions
    private final long EXPIRATION = 1000L * 60 * 60 * 24;

    public String generateToken(User user) {
        return Jwts.builder()
                .setSubject(user.getEmail())
                .claim("role", user.getRole().name())
                .claim("id", user.getId())
                .claim("name", user.getName())
        .setIssuedAt(new Date())
        .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION))
        .signWith(key, SignatureAlgorithm.HS256)
        .compact();
    }

    public String extractEmail(String token) {
        try {
            return getClaims(token).getSubject();
        } catch (io.jsonwebtoken.ExpiredJwtException ex) {
            return null;
        }
    }

    public String extractRole(String token) {
        try {
            return getClaims(token).get("role", String.class);
        } catch (io.jsonwebtoken.ExpiredJwtException ex) {
            return null;
        }
    }

    public boolean validateToken(String token) {
        try {
            getClaims(token);
            return true;
        } catch (JwtException e) {
            return false;
        }
    }

    private Claims getClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .setAllowedClockSkewSeconds(60) // tolerate small clock drift
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
