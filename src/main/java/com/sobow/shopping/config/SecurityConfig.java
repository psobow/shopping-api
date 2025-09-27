package com.sobow.shopping.config;

import com.sobow.shopping.controllers.user.requests.PasswordRequest;
import com.sobow.shopping.controllers.user.requests.UserAddressCreateRequest;
import com.sobow.shopping.controllers.user.requests.UserProfileCreateRequest;
import com.sobow.shopping.controllers.user.requests.admin.AdminUserCreateRequest;
import com.sobow.shopping.controllers.user.requests.admin.UserAuthoritiesRequest;
import com.sobow.shopping.controllers.user.requests.admin.UserAuthorityRequest;
import com.sobow.shopping.security.UserDetailsServiceImpl;
import com.sobow.shopping.services.user.AdminService;
import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
            .authorizeHttpRequests(requests -> requests
                .requestMatchers("/api/admin/**").hasRole("ADMIN")
                .anyRequest().authenticated())
            .httpBasic(basic -> {})
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .csrf(csrf -> csrf.disable())
        ;
        return httpSecurity.build();
    }
    
    @Bean
    public UserDetailsService userDetailsService(AdminService adminService) {
        
        UserDetailsServiceImpl userDetailsService = new UserDetailsServiceImpl(adminService);
        String adminEmail = "admin@email.com";
        String password = "password";
        if (!adminService.userExistsByEmail(adminEmail)) {
            
            UserAddressCreateRequest address = new UserAddressCreateRequest(
                "Wroclaw", "Street", "20", "11-222");
            
            UserProfileCreateRequest userProfile = new UserProfileCreateRequest("Patryk", "Lastname", address);
            UserAuthorityRequest authority = new UserAuthorityRequest("ADMIN");
            AdminUserCreateRequest user = new AdminUserCreateRequest(adminEmail, new PasswordRequest(password), userProfile,
                                                                     new UserAuthoritiesRequest(List.of(authority)));
            
            adminService.adminCreate(user);
        }
        
        return userDetailsService;
    }
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }
}
