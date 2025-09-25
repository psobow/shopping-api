package com.sobow.shopping.services.Impl;

import com.sobow.shopping.domain.user.User;
import com.sobow.shopping.exceptions.EmailAlreadyExistsException;
import com.sobow.shopping.exceptions.InvalidOldPasswordException;
import com.sobow.shopping.exceptions.NoAuthenticationException;
import com.sobow.shopping.repositories.UserRepository;
import com.sobow.shopping.security.UserDetailsImpl;
import com.sobow.shopping.services.CurrentUserService;
import jakarta.annotation.Nullable;
import java.util.Collection;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class CurrentUserServiceImpl implements CurrentUserService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    
    @Override
    public Authentication getAuthentication() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) throw new NoAuthenticationException();
        return auth;
    }
    
    @Override
    public User getAuthenticatedUser(Authentication auth) {
        String email = auth.getName();
        return userRepository.findByEmail(email)
                             .orElseThrow(() -> new UsernameNotFoundException(
                                 "User with email: " + email + " not found"));
    }
    
    @Override
    public void updateSecurityContext(User updatedUser, Collection<? extends GrantedAuthority> authorities) {
        UserDetails updatedPrincipal = new UserDetailsImpl(updatedUser);
        Authentication newAuth =
            new UsernamePasswordAuthenticationToken(updatedPrincipal, null, authorities);
        SecurityContextHolder.getContext().setAuthentication(newAuth);
    }
    
    @Override
    public void assertNewEmailAvailable(String newEmail, @Nullable Long existingUserId) {
        boolean duplicate =
            (existingUserId == null && userRepository.existsByEmail(newEmail)) ||
                (existingUserId != null && userRepository.existsByEmailAndIdNot(newEmail, existingUserId));
        
        if (duplicate) throw new EmailAlreadyExistsException(newEmail);
    }
    
    @Override
    public void assertPasswordMatch(String rawOldPassword, String encodedPassword) {
        if (!passwordEncoder.matches(rawOldPassword, encodedPassword)) {
            throw new InvalidOldPasswordException();
        }
    }
}
