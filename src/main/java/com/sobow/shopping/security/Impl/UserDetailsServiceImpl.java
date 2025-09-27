package com.sobow.shopping.security.Impl;

import com.sobow.shopping.domain.user.User;
import com.sobow.shopping.services.user.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {
    
    private final AdminService adminService;
    
    @Transactional(readOnly = true)
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = adminService.findByEmailWithAuthorities(email);
        return new UserDetailsImpl(user);
    }
}
