package com.sobow.shopping.controllers.user;

import com.sobow.shopping.controllers.ApiResponseDto;
import com.sobow.shopping.controllers.user.requests.admin.AdminUserAuthoritiesUpdateRequest;
import com.sobow.shopping.controllers.user.requests.admin.AdminUserCreateRequest;
import com.sobow.shopping.controllers.user.responses.UserResponse;
import com.sobow.shopping.domain.user.User;
import com.sobow.shopping.services.user.AdminService;
import com.sobow.shopping.services.user.CurrentUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("${api.prefix}/admin/users")
@Tag(name = "User Management Controller", description = "API for managing users account by Admin")
public class UserManagementController {
    
    private final AdminService adminService;
    private final CurrentUserService currentUserService;
    
    @Operation(
        summary = "Create a new user (admin)",
        security = {@SecurityRequirement(name = "bearerAuth")}
    )
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "User created"),
        @ApiResponse(responseCode = "400", description = "Validation error"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden (requires admin role)"),
        @ApiResponse(responseCode = "409", description = "Email already exists")
    })
    @PostMapping("/register")
    public ResponseEntity<ApiResponseDto> adminCreate(
        @RequestBody @Valid AdminUserCreateRequest createRequest
    ) {
        User user = adminService.adminCreate(createRequest);
        UserResponse response = currentUserService.mapToUserResponse(user.getId());
        return ResponseEntity.status(HttpStatus.CREATED)
                             .body(new ApiResponseDto("User created by Admin", response));
    }
    
    @Operation(
        summary = "Find user by email (admin)",
        security = {@SecurityRequirement(name = "bearerAuth")}
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Found"),
        @ApiResponse(responseCode = "400", description = "Validation error"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden (requires admin role)"),
        @ApiResponse(responseCode = "404", description = "User not found")
    })
    @GetMapping(params = "email")
    public ResponseEntity<ApiResponseDto> adminFindByEmail(
        @RequestParam @NotBlank @Email String email
    ) {
        User user = adminService.findByEmail(email);
        UserResponse response = currentUserService.mapToUserResponse(user.getId());
        return ResponseEntity.ok(new ApiResponseDto("Found", response));
    }
    
    @Operation(
        summary = "Update user authorities by email (admin)",
        security = {@SecurityRequirement(name = "bearerAuth")}
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Updated"),
        @ApiResponse(responseCode = "400", description = "Validation error"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden (requires admin role)"),
        @ApiResponse(responseCode = "404", description = "User not found")
    })
    @PutMapping(params = "email")
    public ResponseEntity<ApiResponseDto> adminUpdateAuthoritiesByEmail(
        @RequestParam @NotBlank @Email String email,
        @RequestBody @Valid AdminUserAuthoritiesUpdateRequest updateRequest
    ) {
        User user = adminService.findByEmailWithAuthorities(email);
        user.updateFrom(updateRequest.authorities().value());
        UserResponse response = currentUserService.mapToUserResponse(user.getId());
        return ResponseEntity.ok(new ApiResponseDto("Updated", response));
    }
}
