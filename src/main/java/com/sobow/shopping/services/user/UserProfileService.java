package com.sobow.shopping.services.user;

import com.sobow.shopping.domain.user.UserProfile;

public interface UserProfileService {
    
    UserProfile findByUserId(long userId);
}
