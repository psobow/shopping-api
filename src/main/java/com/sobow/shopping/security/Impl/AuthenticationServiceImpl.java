package com.sobow.shopping.security.Impl;

import com.sobow.shopping.security.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class AuthenticationServiceImpl implements AuthenticationService {
    
    private final AuthenticationManager authenticationManager;
    private final UserDetailsServiceImpl userDetailsService;
    
    @Override
    public UserDetailsImpl authenticate(String email, String password) {
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(email, password)
        );
        return (UserDetailsImpl) userDetailsService.loadUserByUsername(email);
    }
}
