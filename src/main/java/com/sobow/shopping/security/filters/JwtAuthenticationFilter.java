package com.sobow.shopping.security.filters;

import com.sobow.shopping.security.Impl.UserDetailsImpl;
import com.sobow.shopping.security.JwtService;
import com.sobow.shopping.services.user.CurrentUserService;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    
    private static final String BEARER = "Bearer ";
    
    private final UserDetailsService userDetailsService;
    private final JwtService jwtService;
    private final CurrentUserService currentUserService;
    
    @Override
    protected void doFilterInternal(
        HttpServletRequest request,
        HttpServletResponse response,
        FilterChain filterChain
    ) throws ServletException, IOException {
        // Skip if empty token
        Optional<String> tokenOptional = extractToken(request);
        if (tokenOptional.isEmpty()) {
            filterChain.doFilter(request, response);
            return;
        }
        
        try {
            String email = jwtService.validateAccessAndExtractSubject(tokenOptional.get());
            UserDetailsImpl userDetails = (UserDetailsImpl) userDetailsService.loadUserByUsername(email);
            currentUserService.updateSecurityContext(userDetails.getUser(), userDetails.getAuthorities());
        } catch (
            JwtException ex) {
            log.warn("Invalid JWT", ex);
        }
        
        filterChain.doFilter(request, response);
    }
    
    private Optional<String> extractToken(HttpServletRequest request) {
        String header = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (header != null && header.startsWith(BEARER)) {
            return Optional.of(header.substring(BEARER.length()));
        }
        return Optional.empty();
    }
}
