package com.sobow.shopping.controllers.admin;

import com.sobow.shopping.domain.ApiResponse;
import com.sobow.shopping.domain.user.User;
import com.sobow.shopping.domain.user.requests.admin.AdminUserAuthoritiesUpdateRequest;
import com.sobow.shopping.domain.user.requests.admin.AdminUserCreateRequest;
import com.sobow.shopping.domain.user.responses.UserResponse;
import com.sobow.shopping.services.AdminService;
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
public class UserManagementController {
    
    private final AdminService adminService;
    
    @PostMapping
    public ResponseEntity<ApiResponse> adminCreate(@RequestBody @Valid AdminUserCreateRequest createRequest) {
        User user = adminService.adminCreate(createRequest);
        UserResponse response = adminService.mapToUserResponse(user);
        return ResponseEntity.status(HttpStatus.CREATED)
                             .body(new ApiResponse("User created by Admin", response));
    }
    
    @GetMapping(params = "email")
    public ResponseEntity<ApiResponse> adminFindByEmail(@RequestParam @NotBlank @Email String email) {
        User user = adminService.findByEmail(email);
        UserResponse response = adminService.mapToUserResponse(user);
        return ResponseEntity.ok(new ApiResponse("Found", response));
    }
    
    @PutMapping(params = "email")
    public ResponseEntity<ApiResponse> adminUpdateAuthoritiesByEmail(
        @RequestParam @NotBlank @Email String email,
        @RequestBody @Valid AdminUserAuthoritiesUpdateRequest updateRequest
    ) {
        User user = adminService.findByEmail(email);
        user.updateFrom(updateRequest.authorities().value());
        UserResponse response = adminService.mapToUserResponse(user);
        return ResponseEntity.ok(new ApiResponse("Updated", response));
    }
}
