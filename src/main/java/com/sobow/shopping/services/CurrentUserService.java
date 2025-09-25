package com.sobow.shopping.services;

import com.sobow.shopping.domain.user.User;
import jakarta.annotation.Nullable;
import java.util.Collection;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

public interface CurrentUserService {
    
    Authentication getAuthentication();
    
    User getAuthenticatedUser(Authentication auth);
    
    void updateSecurityContext(User updatedUser, Collection<? extends GrantedAuthority> authorities);
    
    void assertNewEmailAvailable(String newEmail, @Nullable Long existingUserId);
    
    void assertPasswordMatch(String rawOldPassword, String encodedPassword);
}
