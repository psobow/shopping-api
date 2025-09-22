package com.sobow.shopping.services;

import com.sobow.shopping.domain.user.User;
import com.sobow.shopping.domain.user.dto.UserCreateRequest;
import com.sobow.shopping.domain.user.dto.UserUpdateRequest;

public interface UserManagementService {
    
    User findByEmailWithAuthorities(String email);
    
    User findByEmail(String email);
    
    User create(UserCreateRequest createRequest);
    
    User partialUpdate(UserUpdateRequest updateRequest);
    
    void updatePassword(String oldPassword, String newPassword);
    
    void updateEmail(String oldPassword, String newEmail);
    
    void deleteByEmail(String email);
    
    boolean userExistsByEmail(String email);
}
