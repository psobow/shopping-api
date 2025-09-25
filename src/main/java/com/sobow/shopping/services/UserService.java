package com.sobow.shopping.services;

import com.sobow.shopping.domain.user.User;
import com.sobow.shopping.domain.user.requests.self.SelfEmailUpdateRequest;
import com.sobow.shopping.domain.user.requests.self.SelfPasswordUpdateRequest;
import com.sobow.shopping.domain.user.requests.self.SelfUserCreateRequest;
import com.sobow.shopping.domain.user.requests.self.SelfUserDeleteRequest;
import com.sobow.shopping.domain.user.requests.self.SelfUserPartialUpdateRequest;
import com.sobow.shopping.domain.user.responses.UserResponse;

public interface UserService {
    
    UserResponse mapToUserResponse(User user);
    
    User selfCreate(SelfUserCreateRequest createRequest);
    
    User selfPartialUpdate(SelfUserPartialUpdateRequest updateRequest);
    
    void selfUpdatePassword(SelfPasswordUpdateRequest updateRequest);
    
    void selfUpdateEmail(SelfEmailUpdateRequest updateRequest);
    
    void selfDelete(SelfUserDeleteRequest deleteRequest);
    
}
