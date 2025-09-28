package com.sobow.shopping.security;

import com.sobow.shopping.security.Impl.UserDetailsImpl;

public interface JwtService {
    
    String generateAccess(UserDetailsImpl userDetails);
    
    String generateRefresh(UserDetailsImpl userDetails);
    
    String validateAccessAndExtractSubject(String token);
    
    String validateRefreshAndExtractSubject(String token);
}
