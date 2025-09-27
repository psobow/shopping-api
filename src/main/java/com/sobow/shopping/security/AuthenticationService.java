package com.sobow.shopping.security;

import com.sobow.shopping.security.Impl.UserDetailsImpl;

public interface AuthenticationService {
    
    UserDetailsImpl authenticate(String email, String password);
}
