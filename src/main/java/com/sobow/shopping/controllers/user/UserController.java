package com.sobow.shopping.controllers.user;

import com.sobow.shopping.domain.ApiResponse;
import com.sobow.shopping.domain.user.User;
import com.sobow.shopping.domain.user.requests.self.SelfEmailUpdateRequest;
import com.sobow.shopping.domain.user.requests.self.SelfPasswordUpdateRequest;
import com.sobow.shopping.domain.user.requests.self.SelfUserCreateRequest;
import com.sobow.shopping.domain.user.requests.self.SelfUserDeleteRequest;
import com.sobow.shopping.domain.user.requests.self.SelfUserPartialUpdateRequest;
import com.sobow.shopping.domain.user.responses.UserResponse;
import com.sobow.shopping.security.UserDetailsImpl;
import com.sobow.shopping.services.UserService;
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
    
    private final UserService userService;
    
    @PostMapping
    public ResponseEntity<ApiResponse> selfCreate(@RequestBody @Valid SelfUserCreateRequest createRequest) {
        User user = userService.selfCreate(createRequest);
        UserResponse response = userService.mapToUserResponse(user.getId());
        return ResponseEntity.status(HttpStatus.CREATED)
                             .body(new ApiResponse("User created", response));
    }
    
    @GetMapping(path = "/me")
    public ResponseEntity<ApiResponse> selfGet(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        User user = userDetails.getUser();
        UserResponse response = userService.mapToUserResponse(user.getId());
        return ResponseEntity.ok(new ApiResponse("Logged in user", response));
    }
    
    @PutMapping(path = "/me")
    public ResponseEntity<ApiResponse> selfPartialUpdate(@RequestBody @Valid SelfUserPartialUpdateRequest updateRequest) {
        User user = userService.selfPartialUpdate(updateRequest);
        UserResponse response = userService.mapToUserResponse(user.getId());
        return ResponseEntity.ok(new ApiResponse("User updated", response));
    }
    
    @PostMapping(path = "/me/password")
    public ResponseEntity<Void> selfUpdatePassword(@RequestBody @Valid SelfPasswordUpdateRequest updateRequest) {
        userService.selfUpdatePassword(updateRequest);
        return ResponseEntity.noContent().build();
    }
    
    @PostMapping(path = "/me/email")
    public ResponseEntity<Void> selfUpdateEmail(@RequestBody @Valid SelfEmailUpdateRequest updateRequest) {
        userService.selfUpdateEmail(updateRequest);
        return ResponseEntity.noContent().build();
    }
    
    @DeleteMapping(path = "/me")
    public ResponseEntity<Void> selfDelete(@RequestBody @Valid SelfUserDeleteRequest deleteRequest) {
        userService.selfDelete(deleteRequest);
        return ResponseEntity.noContent().build();
    }
}
