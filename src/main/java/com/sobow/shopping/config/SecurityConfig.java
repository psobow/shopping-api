package com.sobow.shopping.config;

import com.sobow.shopping.domain.user.requests.admin.AdminCreateUserRequest;
import com.sobow.shopping.domain.user.requests.admin.AuthorityDto;
import com.sobow.shopping.domain.user.requests.admin.ListAuthorityDto;
import com.sobow.shopping.domain.user.requests.shared.CreateUserAddressDto;
import com.sobow.shopping.domain.user.requests.shared.CreateUserProfileDto;
import com.sobow.shopping.domain.user.requests.shared.PasswordDto;
import com.sobow.shopping.security.UserDetailsServiceImpl;
import com.sobow.shopping.services.AdminService;
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
            
            CreateUserAddressDto address = new CreateUserAddressDto(
                "Wroclaw", "Street", "20", "11-222");
            
            CreateUserProfileDto userProfile = new CreateUserProfileDto("Patryk", "Lastname", address);
            AuthorityDto authority = new AuthorityDto("ADMIN");
            AdminCreateUserRequest user = new AdminCreateUserRequest(adminEmail, new PasswordDto(password), userProfile,
                                                                     new ListAuthorityDto(List.of(authority)));
            
            adminService.adminCreate(user);
        }
        
        return userDetailsService;
    }
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }
}
