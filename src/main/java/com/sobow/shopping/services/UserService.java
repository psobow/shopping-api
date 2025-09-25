package com.sobow.shopping.services;

import com.sobow.shopping.domain.user.User;
import com.sobow.shopping.domain.user.requests.self.SelfCreateUserRequest;
import com.sobow.shopping.domain.user.requests.self.SelfDeleteUserRequest;
import com.sobow.shopping.domain.user.requests.self.SelfUpdateEmailRequest;
import com.sobow.shopping.domain.user.requests.self.SelfUpdatePasswordRequest;
import com.sobow.shopping.domain.user.requests.self.SelfUpdateUserRequest;
import com.sobow.shopping.domain.user.responses.UserResponse;

public interface UserService {
    
    UserResponse mapToUserResponse(User user);
    
    User selfCreate(SelfCreateUserRequest createRequest);
    
    User selfPartialUpdate(SelfUpdateUserRequest updateRequest);
    
    void selfUpdatePassword(SelfUpdatePasswordRequest updateRequest);
    
    void selfUpdateEmail(SelfUpdateEmailRequest updateRequest);
    
    void selfDelete(SelfDeleteUserRequest deleteRequest);
    
}
