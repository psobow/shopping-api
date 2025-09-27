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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    
    @Value("${api.prefix}")
    private String apiPrefix;
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
            .authorizeHttpRequests(requests -> requests
                // 1) Admin-only
                .requestMatchers(apiPrefix + "/admin/**").hasRole("ADMIN")
                
                // 2) Public (no auth)
                .requestMatchers(HttpMethod.POST, apiPrefix + "/users/register").permitAll()
                .requestMatchers(HttpMethod.GET,
                                 apiPrefix + "/csrf",
                                 apiPrefix + "/categories",
                                 apiPrefix + "/categories/**",
                                 apiPrefix + "/products",
                                 apiPrefix + "/products/**"
                ).permitAll()
                
                // 3) Regular user and admin
                .requestMatchers(apiPrefix + "/users/**").hasAnyRole("USER", "ADMIN")
                
                // 4) Fallback
                .anyRequest().authenticated()
            )
            .httpBasic(basic -> {})
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            //.csrf(csrf -> csrf.disable())
            .csrf(csrf -> csrf.csrfTokenRepository(
                CookieCsrfTokenRepository.withHttpOnlyFalse()
            ))
        ;
        return httpSecurity.build();
    }
    
    @Bean
    public UserDetailsService userDetailsService(AdminService adminService) {
        
        UserDetailsServiceImpl userDetailsService = new UserDetailsServiceImpl(adminService);
        String adminEmail = "user@email.com";
        String password = "password";
        if (!adminService.userExistsByEmail(adminEmail)) {
            
            UserAddressCreateRequest address = new UserAddressCreateRequest(
                "Wroclaw", "Street", "20", "11-222");
            
            UserProfileCreateRequest userProfile = new UserProfileCreateRequest("Patryk", "Lastname", address);
            UserAuthorityRequest authority = new UserAuthorityRequest("USER");
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
