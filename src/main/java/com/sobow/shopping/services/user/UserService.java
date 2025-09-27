package com.sobow.shopping.services.user;

import com.sobow.shopping.controllers.user.requests.self.SelfEmailUpdateRequest;
import com.sobow.shopping.controllers.user.requests.self.SelfPasswordUpdateRequest;
import com.sobow.shopping.controllers.user.requests.self.SelfUserCreateRequest;
import com.sobow.shopping.controllers.user.requests.self.SelfUserDeleteRequest;
import com.sobow.shopping.controllers.user.requests.self.SelfUserPartialUpdateRequest;
import com.sobow.shopping.controllers.user.responses.UserResponse;
import com.sobow.shopping.domain.user.User;

public interface UserService {
    
    UserResponse mapToUserResponse(long userId);
    
    User selfCreate(SelfUserCreateRequest createRequest);
    
    User selfPartialUpdate(SelfUserPartialUpdateRequest updateRequest);
    
    void selfUpdatePassword(SelfPasswordUpdateRequest updateRequest);
    
    void selfUpdateEmail(SelfEmailUpdateRequest updateRequest);
    
    void selfDelete(SelfUserDeleteRequest deleteRequest);
    
}
