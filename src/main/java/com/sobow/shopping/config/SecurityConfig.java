package com.sobow.shopping.config;

import com.sobow.shopping.domain.user.dto.UserAddressCreateRequest;
import com.sobow.shopping.domain.user.dto.UserAuthorityRequest;
import com.sobow.shopping.domain.user.dto.UserCreateRequest;
import com.sobow.shopping.domain.user.dto.UserProfileCreateRequest;
import com.sobow.shopping.security.UserDetailsServiceImpl;
import com.sobow.shopping.services.UserManagementService;
import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity.authorizeHttpRequests(requests -> requests.anyRequest().permitAll());
        return httpSecurity.build();
    }
    
    @Bean
    public UserDetailsService userDetailsService(UserManagementService userManagementService) {
        
        UserDetailsServiceImpl userDetailsService = new UserDetailsServiceImpl(userManagementService);
        
        if (!userManagementService.userExistsByEmail("address@email.com")) {
            
            UserAddressCreateRequest address = new UserAddressCreateRequest(
                "Wroclaw", "Street", "20", "11-222");
            UserProfileCreateRequest userProfile = new UserProfileCreateRequest("Patryk", "Tak", address);
            UserAuthorityRequest authority = new UserAuthorityRequest("ADMIN");
            UserCreateRequest user = new UserCreateRequest("address@email.com", "password", userProfile, List.of(authority));
            
            userManagementService.create(user);
        }
        
        return userDetailsService;
    }
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }
}
