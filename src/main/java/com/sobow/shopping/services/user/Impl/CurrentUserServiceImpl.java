package com.sobow.shopping.services.user.Impl;

import com.sobow.shopping.controllers.user.responses.UserResponse;
import com.sobow.shopping.domain.user.User;
import com.sobow.shopping.domain.user.repo.UserRepository;
import com.sobow.shopping.exceptions.EmailAlreadyExistsException;
import com.sobow.shopping.exceptions.InvalidOldPasswordException;
import com.sobow.shopping.mappers.user.responses.UserResponseMapper;
import com.sobow.shopping.security.Impl.UserDetailsImpl;
import com.sobow.shopping.services.user.CurrentUserService;
import jakarta.annotation.Nullable;
import jakarta.persistence.EntityNotFoundException;
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
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Component
public class CurrentUserServiceImpl implements CurrentUserService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserResponseMapper userResponseMapper;
    
    @Transactional
    @Override
    public UserResponse mapToUserResponse(long userId) {
        User user = userRepository.findById(userId)
                                  .orElseThrow(() -> new EntityNotFoundException("User with id" + userId + " not found"));
        return userResponseMapper.mapToDto(user);
    }
    
    @Override
    public Authentication getAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }
    
    @Override
    public User getAuthenticatedUser(Authentication auth) {
        String email = auth.getName();
        return userRepository.findByEmail(email)
                             .orElseThrow(() -> new UsernameNotFoundException(
                                 "User with email: " + email + " not found"));
    }
    
    @Override
    public void updateSecurityContext(User user, Collection<? extends GrantedAuthority> authorities) {
        UserDetails newPrincipal = new UserDetailsImpl(user);
        Authentication newAuth =
            new UsernamePasswordAuthenticationToken(newPrincipal, null, authorities);
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
