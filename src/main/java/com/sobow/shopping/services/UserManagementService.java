package com.sobow.shopping.services;

import com.sobow.shopping.domain.user.User;
import com.sobow.shopping.domain.user.requests.UserCreateRequest;
import com.sobow.shopping.domain.user.requests.UserUpdateRequest;

public interface UserManagementService {
    
    User findByEmailWithAuthorities(String email);
    
    User findByEmail(String email);
    
    User create(UserCreateRequest createRequest);
    
    User selfPartialUpdate(UserUpdateRequest updateRequest);
    
    void selfUpdatePassword(String oldPassword, String newPassword);
    
    void selfUpdateEmail(String oldPassword, String newEmail);
    
    void deleteByEmail(String email);
    
    boolean userExistsByEmail(String email);
}
