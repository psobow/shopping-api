package com.sobow.shopping.controllers.security;

import com.sobow.shopping.controllers.ApiResponseDto;
import com.sobow.shopping.controllers.security.dto.JwtAuthRequest;
import com.sobow.shopping.controllers.security.dto.JwtAuthResponse;
import com.sobow.shopping.controllers.security.dto.JwtRefreshTokenRequest;
import com.sobow.shopping.security.AuthenticationService;
import com.sobow.shopping.security.Impl.UserDetailsImpl;
import com.sobow.shopping.security.JwtService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Auth Controller", description = "API for generating JWT")
public class AuthController {
    
    private final AuthenticationService authenticationService;
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;
    
    @Operation(
        summary = "Login - Generate access and refresh token",
        requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
            required = true,
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(value = """
                            {
                                "email": "adminuser@gmail.com",
                                "password": "password"
                            }
                    """
                )
            )
        )
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "New JWTs generated"),
        @ApiResponse(responseCode = "400", description = "Validation error"),
        @ApiResponse(responseCode = "401", description = "Invalid credentials")
    })
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
    
    @Operation(
        summary = "Refresh – Generate a new access token",
        requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
            required = true,
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(value = """
                    {
                      "refresh": "refresh token value"
                    }
                    """
                )
            )
        )
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "New JWT access generated"),
        @ApiResponse(responseCode = "400", description = "Validation error"),
        @ApiResponse(responseCode = "401", description = "Invalid or expired refresh token")
    })
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
