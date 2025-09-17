package com.sobow.shopping.services;

import com.sobow.shopping.domain.entities.UserProfile;

public interface UserProfileService {
    
    UserProfile findByUserId(long userId);
}
