package com.sobow.shopping.services;

import com.sobow.shopping.domain.user.User;
import com.sobow.shopping.domain.user.requests.SelfUpdateEmailRequest;
import com.sobow.shopping.domain.user.requests.SelfUpdatePasswordRequest;
import com.sobow.shopping.domain.user.requests.SelfUserDeleteRequest;
import com.sobow.shopping.domain.user.requests.SelfUserUpdateRequest;
import com.sobow.shopping.domain.user.requests.UserCreateRequest;

public interface UserManagementService {
    
    User findByEmailWithAuthorities(String email);
    
    User findByEmail(String email);
    
    User create(UserCreateRequest createRequest);
    
    User selfPartialUpdate(SelfUserUpdateRequest updateRequest);
    
    void selfUpdatePassword(SelfUpdatePasswordRequest updateRequest);
    
    void selfUpdateEmail(SelfUpdateEmailRequest updateRequest);
    
    void selfDelete(SelfUserDeleteRequest deleteRequest);
    
    boolean userExistsByEmail(String email);
}
