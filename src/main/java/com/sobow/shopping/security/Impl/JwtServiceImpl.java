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
    
    @Value("${jwt.secret.access}")
    private String secretAccessKey;
    
    @Value("${jwt.secret.refresh}")
    private String secretRefreshKey;
    
    private static final long TOKEN_ACCESS_VALIDITY_IN_MS = Duration.ofHours(1).toMillis();
    private static final long TOKEN_REFRESH_VALIDITY_IN_MS = Duration.ofDays(7).toMillis();
    
    public static final String CLAIM_KEY_TYPE = "type";
    private static final String TOKEN_TYPE_ACCESS = "access";
    private static final String TOKEN_TYPE_REFRESH = "refresh";
    
    @Override
    public String generateAccess(UserDetailsImpl userDetails) {
        return generateToken(userDetails, TOKEN_ACCESS_VALIDITY_IN_MS, secretAccessKey, TOKEN_TYPE_ACCESS);
    }
    
    @Override
    public String generateRefresh(UserDetailsImpl userDetails) {
        return generateToken(userDetails, TOKEN_REFRESH_VALIDITY_IN_MS, secretRefreshKey, TOKEN_TYPE_REFRESH);
    }
    
    private String generateToken(
        UserDetailsImpl userDetails,
        long tokenValidityInMs,
        String secretKey,
        String tokenType
    ) {
        Date now = new Date();
        Date expiration = new Date(now.getTime() + tokenValidityInMs);
        User user = userDetails.getUser();
        return Jwts.builder()
                   .subject(user.getEmail())
                   .issuedAt(now)
                   .expiration(expiration)
                   .signWith(getSigningKey(secretKey))
                   .claim(CLAIM_KEY_TYPE, tokenType)
                   .compact();
    }
    
    @Override
    public String validateAccessAndExtractSubject(String token) {
        return validateTokenAndExtractSubject(token, secretAccessKey, TOKEN_TYPE_ACCESS);
    }
    
    @Override
    public String validateRefreshAndExtractSubject(String token) {
        return validateTokenAndExtractSubject(token, secretRefreshKey, TOKEN_TYPE_REFRESH);
    }
    
    private String validateTokenAndExtractSubject(
        String token,
        String secretKey,
        String expectedTokenType
    ) {
        Claims payload = Jwts.parser()
                             .verifyWith(getSigningKey(secretKey))
                             .build()
                             .parseSignedClaims(token)
                             .getPayload();
        
        String tokenType = payload.get(CLAIM_KEY_TYPE, String.class);
        if (tokenType == null || tokenType.isBlank() || !tokenType.equals(expectedTokenType)) {
            throw new JwtException("Invalid token type");
        }
        String sub = payload.getSubject();
        if (sub == null || sub.isBlank()) {
            throw new JwtException("Missing subject");
        }
        Date exp = payload.getExpiration();
        if (exp != null && Date.from(Instant.now()).after(exp)) {
            throw new JwtException("Expired token");
        }
        return payload.getSubject();
    }
    
    private SecretKey getSigningKey(String secretKey) {
        byte[] keyBytes = secretKey.getBytes();
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
