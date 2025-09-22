package com.sobow.shopping.security;

import com.sobow.shopping.domain.user.User;
import com.sobow.shopping.services.UserManagementService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {
    
    private final UserManagementService userManagementService;
    
    @Transactional(readOnly = true)
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userManagementService.findByEmailWithAuthorities(email);
        return new UserDetailsImpl(user);
    }
}
