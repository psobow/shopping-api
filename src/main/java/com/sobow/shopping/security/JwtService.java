package com.sobow.shopping.security;

import com.sobow.shopping.security.Impl.UserDetailsImpl;

public interface JwtService {
    
    String generateToken(UserDetailsImpl userDetails);
}
