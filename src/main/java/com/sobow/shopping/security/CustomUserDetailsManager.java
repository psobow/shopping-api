package com.sobow.shopping.security;

import org.springframework.security.provisioning.UserDetailsManager;

public interface CustomUserDetailsManager extends UserDetailsManager {
    
    void changeEmail(String oldPassword, String newEmail);
}
