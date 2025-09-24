package com.sobow.shopping.config;

import com.sobow.shopping.domain.user.requests.admin.AdminCreateUserRequest;
import com.sobow.shopping.domain.user.requests.admin.AuthorityDto;
import com.sobow.shopping.domain.user.requests.admin.ListAuthorityDto;
import com.sobow.shopping.domain.user.requests.shared.CreateUserAddressDto;
import com.sobow.shopping.domain.user.requests.shared.CreateUserProfileDto;
import com.sobow.shopping.domain.user.requests.shared.PasswordDto;
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
        String adminEmail = "admin@email.com";
        String password = "password";
        if (!userManagementService.userExistsByEmail(adminEmail)) {
            
            CreateUserAddressDto address = new CreateUserAddressDto(
                "Wroclaw", "Street", "20", "11-222");
            
            CreateUserProfileDto userProfile = new CreateUserProfileDto("Patryk", "Lastname", address);
            AuthorityDto authority = new AuthorityDto("ADMIN");
            AdminCreateUserRequest user = new AdminCreateUserRequest(adminEmail, new PasswordDto(password), userProfile,
                                                                     new ListAuthorityDto(List.of(authority)));
            
            userManagementService.adminCreate(user);
        }
        
        return userDetailsService;
    }
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }
}
