package com.sobow.shopping.services;

import com.sobow.shopping.domain.user.User;
import com.sobow.shopping.domain.user.requests.admin.AdminCreateUserRequest;
import com.sobow.shopping.domain.user.requests.self.SelfCreateUserRequest;
import com.sobow.shopping.domain.user.requests.self.SelfDeleteUserRequest;
import com.sobow.shopping.domain.user.requests.self.SelfUpdateEmailRequest;
import com.sobow.shopping.domain.user.requests.self.SelfUpdatePasswordRequest;
import com.sobow.shopping.domain.user.requests.self.SelfUpdateUserRequest;

public interface UserManagementService {
    
    User findByEmailWithAuthorities(String email);
    
    User findByEmail(String email);
    
    User adminCreate(AdminCreateUserRequest createRequest);
    
    User selfCreate(SelfCreateUserRequest createRequest);
    
    User selfPartialUpdate(SelfUpdateUserRequest updateRequest);
    
    void selfUpdatePassword(SelfUpdatePasswordRequest updateRequest);
    
    void selfUpdateEmail(SelfUpdateEmailRequest updateRequest);
    
    void selfDelete(SelfDeleteUserRequest deleteRequest);
    
    boolean userExistsByEmail(String email);
}
