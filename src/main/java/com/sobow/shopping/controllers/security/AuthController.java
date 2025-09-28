package com.sobow.shopping.controllers.security;

import com.sobow.shopping.controllers.ApiResponseDto;
import com.sobow.shopping.controllers.security.dto.JwtAuthRequest;
import com.sobow.shopping.controllers.security.dto.JwtAuthResponse;
import com.sobow.shopping.controllers.security.dto.JwtRefreshTokenRequest;
import com.sobow.shopping.security.AuthenticationService;
import com.sobow.shopping.security.Impl.UserDetailsImpl;
import com.sobow.shopping.security.JwtService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("${api.prefix}/users/auth")
public class AuthController {
    
    private final AuthenticationService authenticationService;
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;
    
    @PostMapping("/login")
    public ResponseEntity<ApiResponseDto> login(
        @RequestBody @Valid JwtAuthRequest authRequest
    ) {
        UserDetailsImpl userDetails = authenticationService.authenticate(authRequest.email(), authRequest.password());
        String accessTokenValue = jwtService.generateAccess(userDetails);
        String refreshTokenValue = jwtService.generateRefresh(userDetails);
        JwtAuthResponse response = new JwtAuthResponse(accessTokenValue, refreshTokenValue);
        return ResponseEntity.ok(new ApiResponseDto("New JWTs generated", response));
    }
    
    @PostMapping("/refresh")
    public ResponseEntity<ApiResponseDto> refresh(
        @RequestBody @Valid JwtRefreshTokenRequest refreshTokenRequest
    ) {
        String email = jwtService.validateRefreshAndExtractSubject(refreshTokenRequest.refresh());
        UserDetailsImpl userDetails = (UserDetailsImpl) userDetailsService.loadUserByUsername(email);
        String newAccessToken = jwtService.generateAccess(userDetails);
        return ResponseEntity.ok(
            new ApiResponseDto("New JWT access generated", new JwtAuthResponse(newAccessToken, null))
        );
    }
}
