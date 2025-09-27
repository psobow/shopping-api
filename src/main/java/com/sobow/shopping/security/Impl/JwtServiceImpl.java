package com.sobow.shopping.security.Impl;

import com.sobow.shopping.domain.user.User;
import com.sobow.shopping.security.JwtService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class JwtServiceImpl implements JwtService {
    
    @Value("${jwt.secret}")
    private String secretKey;
    
    private static final long TOKEN_VALIDITY_IN_MS = Duration.ofHours(1).toMillis();
    
    @Override
    public String generateToken(UserDetailsImpl userDetails) {
        Date now = new Date();
        Date expiration = new Date(now.getTime() + TOKEN_VALIDITY_IN_MS);
        
        User user = userDetails.getUser();
        
        return Jwts.builder()
                   .subject(user.getEmail())
                   .issuedAt(now)
                   .expiration(expiration)
                   .signWith(getSigningKey())
                   .compact();
    }
    
    @Override
    public String validateAndExtractSubject(String token) {
        Claims payload = Jwts.parser()
                             .verifyWith(getSigningKey())
                             .build()
                             .parseSignedClaims(token)
                             .getPayload();
        
        String sub = payload.getSubject();
        if (sub == null || sub.isBlank()) {
            throw new JwtException("Missing subject");
        }
        Date exp = payload.getExpiration();
        if (exp != null && exp.before(Date.from(Instant.now()))) {
            throw new JwtException("Expired token");
        }
        
        return payload.getSubject();
    }
    
    private SecretKey getSigningKey() {
        byte[] keyBytes = secretKey.getBytes();
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
