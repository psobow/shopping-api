package com.sobow.shopping.controllers.security;

import com.sobow.shopping.controllers.ApiResponse;
import com.sobow.shopping.controllers.security.dto.JwtAuthRequest;
import com.sobow.shopping.controllers.security.dto.JwtAuthResponse;
import com.sobow.shopping.security.AuthenticationService;
import com.sobow.shopping.security.Impl.UserDetailsImpl;
import com.sobow.shopping.security.JwtService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("${api.prefix}/users")
public class AuthController {
    
    private final AuthenticationService authenticationService;
    private final JwtService jwtService;
    
    @PostMapping("/login")
    public ResponseEntity<ApiResponse> login(
        @RequestBody @Valid JwtAuthRequest authRequest
    ) {
        UserDetailsImpl userDetails = authenticationService.authenticate(authRequest.email(), authRequest.password());
        String tokenValue = jwtService.generateToken(userDetails);
        JwtAuthResponse response = new JwtAuthResponse(tokenValue);
        return ResponseEntity.ok(new ApiResponse("User logged in", response));
    }
}
