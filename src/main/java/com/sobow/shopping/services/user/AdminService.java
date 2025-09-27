package com.sobow.shopping.services.user;

import com.sobow.shopping.controllers.user.requests.admin.AdminUserCreateRequest;
import com.sobow.shopping.domain.user.User;

public interface AdminService {
    
    User findByEmailWithAuthorities(String email);
    
    User findByEmail(String email);
    
    boolean userExistsByEmail(String email);
    
    User adminCreate(AdminUserCreateRequest createRequest);
}
