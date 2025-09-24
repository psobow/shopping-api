package com.sobow.shopping.controllers.user;

import com.sobow.shopping.domain.ApiResponse;
import com.sobow.shopping.domain.user.User;
import com.sobow.shopping.domain.user.requests.self.SelfCreateUserRequest;
import com.sobow.shopping.domain.user.requests.self.SelfDeleteUserRequest;
import com.sobow.shopping.domain.user.requests.self.SelfUpdateEmailRequest;
import com.sobow.shopping.domain.user.requests.self.SelfUpdatePasswordRequest;
import com.sobow.shopping.domain.user.requests.self.SelfUpdateUserRequest;
import com.sobow.shopping.domain.user.responses.UserResponse;
import com.sobow.shopping.mappers.Mapper;
import com.sobow.shopping.security.UserDetailsImpl;
import com.sobow.shopping.services.UserManagementService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping(path = "${api.prefix}/users")
public class UserController {
    
    private final Mapper<User, UserResponse> userResponseMapper;
    private final UserManagementService userManagementService;
    
    @PostMapping
    public ResponseEntity<ApiResponse> selfCreate(@RequestBody @Valid SelfCreateUserRequest createRequest) {
        User user = userManagementService.selfCreate(createRequest);
        UserResponse response = userResponseMapper.mapToDto(user);
        return ResponseEntity.status(HttpStatus.CREATED)
                             .body(new ApiResponse("User created", response));
    }
    
    @GetMapping(path = "/me")
    public ResponseEntity<ApiResponse> selfGet(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        User user = userDetails.getUser();
        UserResponse response = userResponseMapper.mapToDto(user);
        return ResponseEntity.ok(new ApiResponse("Logged in user", response));
    }
    
    @PutMapping(path = "/me")
    public ResponseEntity<ApiResponse> selfPartialUpdate(@RequestBody @Valid SelfUpdateUserRequest updateRequest) {
        User user = userManagementService.selfPartialUpdate(updateRequest);
        UserResponse response = userResponseMapper.mapToDto(user);
        return ResponseEntity.ok(new ApiResponse("User updated", response));
    }
    
    @PostMapping(path = "/me/password")
    public ResponseEntity<Void> selfUpdatePassword(@RequestBody @Valid SelfUpdatePasswordRequest updateRequest) {
        userManagementService.selfUpdatePassword(updateRequest);
        return ResponseEntity.noContent().build();
    }
    
    @PostMapping(path = "/me/email")
    public ResponseEntity<Void> selfUpdateEmail(@RequestBody @Valid SelfUpdateEmailRequest updateRequest) {
        userManagementService.selfUpdateEmail(updateRequest);
        return ResponseEntity.noContent().build();
    }
    
    @DeleteMapping(path = "/me")
    public ResponseEntity<Void> selfDelete(@RequestBody @Valid SelfDeleteUserRequest deleteRequest) {
        userManagementService.selfDelete(deleteRequest);
        return ResponseEntity.noContent().build();
    }
}
