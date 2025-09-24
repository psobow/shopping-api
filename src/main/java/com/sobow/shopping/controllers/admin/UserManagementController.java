package com.sobow.shopping.controllers.admin;

import com.sobow.shopping.domain.ApiResponse;
import com.sobow.shopping.domain.user.User;
import com.sobow.shopping.domain.user.requests.admin.AdminCreateUserRequest;
import com.sobow.shopping.domain.user.requests.admin.AdminUpdateUserAuthoritiesRequest;
import com.sobow.shopping.domain.user.responses.UserResponse;
import com.sobow.shopping.mappers.Mapper;
import com.sobow.shopping.services.UserManagementService;
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
    
    private final Mapper<User, UserResponse> userResponseMapper;
    private final UserManagementService userManagementService;
    
    @PostMapping
    public ResponseEntity<ApiResponse> adminRegister(@RequestBody @Valid AdminCreateUserRequest createRequest) {
        User user = userManagementService.adminCreate(createRequest);
        UserResponse response = userResponseMapper.mapToDto(user);
        return ResponseEntity.status(HttpStatus.CREATED)
                             .body(new ApiResponse("User created by Admin", response));
    }
    
    @GetMapping(params = "email")
    public ResponseEntity<ApiResponse> adminFindByEmail(@RequestParam @NotBlank @Email String email) {
        User user = userManagementService.findByEmail(email);
        UserResponse response = userResponseMapper.mapToDto(user);
        return ResponseEntity.ok(new ApiResponse("Found", response));
    }
    
    @PutMapping(params = "email")
    public ResponseEntity<ApiResponse> adminUpdateAuthoritiesByEmail(
        @RequestParam @NotBlank @Email String email,
        @RequestBody @Valid AdminUpdateUserAuthoritiesRequest updateRequest
    ) {
        User user = userManagementService.findByEmail(email);
        user.updateFrom(updateRequest.authorities().value());
        UserResponse response = userResponseMapper.mapToDto(user);
        return ResponseEntity.ok(new ApiResponse("Updated", response));
    }
}
