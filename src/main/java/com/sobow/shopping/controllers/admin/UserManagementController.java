package com.sobow.shopping.controllers.admin;

import com.sobow.shopping.domain.ApiResponse;
import com.sobow.shopping.domain.user.User;
import com.sobow.shopping.domain.user.requests.admin.AdminCreateUserRequest;
import com.sobow.shopping.domain.user.responses.UserResponse;
import com.sobow.shopping.mappers.Mapper;
import com.sobow.shopping.services.UserManagementService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("${api.prefix}/admin/users")
public class UserManagementController {
    
    private final Mapper<User, UserResponse> userResponseMapper;
    private final UserManagementService userManagementService;
    
    @PostMapping
    public ResponseEntity<ApiResponse> adminRegister(AdminCreateUserRequest createRequest) {
        User user = userManagementService.adminCreate(createRequest);
        UserResponse response = userResponseMapper.mapToDto(user);
        return ResponseEntity.status(HttpStatus.CREATED)
                             .body(new ApiResponse("User created by Admin", response));
    }
    
    // find by email
    
    // update Authorities by email
}
