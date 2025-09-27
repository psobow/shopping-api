package com.sobow.shopping.services.user.Impl;

import com.sobow.shopping.domain.user.UserProfile;
import com.sobow.shopping.domain.user.UserProfileRepository;
import com.sobow.shopping.services.user.UserProfileService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class UserProfileServiceImpl implements UserProfileService {
    
    private final UserProfileRepository userProfileRepository;
    
    @Override
    public UserProfile findByUserId(long userId) {
        return userProfileRepository.findByUserId(userId)
                                    .orElseThrow(
                                        () -> new EntityNotFoundException("UserProfile for userId " + userId + " does not exist")
                                    );
    }
}
