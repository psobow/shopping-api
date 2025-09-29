package com.sobow.shopping.config;

import com.sobow.shopping.controllers.user.requests.PasswordRequest;
import com.sobow.shopping.controllers.user.requests.UserAddressCreateRequest;
import com.sobow.shopping.controllers.user.requests.UserProfileCreateRequest;
import com.sobow.shopping.controllers.user.requests.admin.AdminUserCreateRequest;
import com.sobow.shopping.controllers.user.requests.admin.UserAuthoritiesRequest;
import com.sobow.shopping.controllers.user.requests.admin.UserAuthorityRequest;
import com.sobow.shopping.security.Impl.UserDetailsServiceImpl;
import com.sobow.shopping.security.JwtService;
import com.sobow.shopping.security.filters.JwtAuthenticationFilter;
import com.sobow.shopping.services.user.AdminService;
import com.sobow.shopping.services.user.CurrentUserService;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    
    @Value("${api.prefix}")
    private String apiPrefix;
    
    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter(
        UserDetailsService userDetailsService,
        JwtService jwtService,
        CurrentUserService currentUserService
    ) {
        return new JwtAuthenticationFilter(userDetailsService, jwtService, currentUserService);
    }
    
    @Bean
    public SecurityFilterChain filterChain(
        HttpSecurity httpSecurity,
        JwtAuthenticationFilter jwtAuthenticationFilter
    ) throws Exception {
        httpSecurity
            .authorizeHttpRequests(requests -> requests
                // 1) Admin-only
                .requestMatchers(apiPrefix + "/admin/**").hasRole("ADMIN")
                
                // 2) Public
                .requestMatchers("/error").permitAll() // need this in order to get proper errors instead of empty 401
                .requestMatchers(
                    HttpMethod.POST,
                    apiPrefix + "/users/register",
                    apiPrefix + "/users/auth/**"
                ).permitAll()
                .requestMatchers(
                    HttpMethod.GET,
                    apiPrefix + "/csrf",
                    apiPrefix + "/categories",
                    apiPrefix + "/categories/**",
                    apiPrefix + "/products",
                    apiPrefix + "/products/**"
                ).permitAll()
                
                // Allow access to Swagger UI
                .requestMatchers(
                    "/swagger-ui.html",
                    "/swagger-ui/**",
                    "/v3/api-docs/**"
                ).permitAll()
                
                // 3) Regular user and admin
                .requestMatchers(apiPrefix + "/users/**").hasAnyRole("USER", "ADMIN")
                
                // 4) Fallback
                .anyRequest().authenticated()
            )
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .csrf(csrf -> csrf.disable())
//            .httpBasic(basic -> {})
//            .csrf(csrf -> csrf.csrfTokenRepository(
//                CookieCsrfTokenRepository.withHttpOnlyFalse()
//            ))
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
        ;
        return httpSecurity.build();
    }
    
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }
    
    @Bean
    public UserDetailsService userDetailsService(AdminService adminService) {
        
        UserDetailsServiceImpl userDetailsService = new UserDetailsServiceImpl(adminService);
        String adminEmail = "adminuser@gmail.com";
        String password = "password";
        if (!adminService.userExistsByEmail(adminEmail)) {
            
            UserAddressCreateRequest address = new UserAddressCreateRequest(
                "Warsaw", "Street", "20", "11-222");
            
            UserProfileCreateRequest userProfile = new UserProfileCreateRequest("John", "Doe", address);
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
