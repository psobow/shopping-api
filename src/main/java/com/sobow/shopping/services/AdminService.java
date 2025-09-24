package com.sobow.shopping.services;

import com.sobow.shopping.domain.user.User;
import com.sobow.shopping.domain.user.requests.admin.AdminCreateUserRequest;

public interface AdminService {
    
    User findByEmailWithAuthorities(String email);
    
    User findByEmail(String email);
    
    boolean userExistsByEmail(String email);
    
    User adminCreate(AdminCreateUserRequest createRequest);
}
