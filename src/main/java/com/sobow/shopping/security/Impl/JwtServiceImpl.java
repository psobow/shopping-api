package com.sobow.shopping.security.Impl;

import com.sobow.shopping.domain.user.User;
import com.sobow.shopping.security.JwtService;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import java.time.Duration;
import java.util.Date;
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
    
    private Key getSigningKey() {
        byte[] keyBytes = secretKey.getBytes();
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
