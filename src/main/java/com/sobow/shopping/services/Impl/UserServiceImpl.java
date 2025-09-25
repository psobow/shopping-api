package com.sobow.shopping.services.Impl;

import com.sobow.shopping.domain.user.User;
import com.sobow.shopping.domain.user.UserAuthority;
import com.sobow.shopping.domain.user.requests.self.SelfCreateUserRequest;
import com.sobow.shopping.domain.user.requests.self.SelfDeleteUserRequest;
import com.sobow.shopping.domain.user.requests.self.SelfUpdateEmailRequest;
import com.sobow.shopping.domain.user.requests.self.SelfUpdatePasswordRequest;
import com.sobow.shopping.domain.user.requests.self.SelfUpdateUserRequest;
import com.sobow.shopping.domain.user.responses.UserResponse;
import com.sobow.shopping.exceptions.EmailAlreadyExistsException;
import com.sobow.shopping.exceptions.InvalidOldPasswordException;
import com.sobow.shopping.exceptions.NoAuthenticationException;
import com.sobow.shopping.mappers.Mapper;
import com.sobow.shopping.repositories.UserRepository;
import com.sobow.shopping.security.UserDetailsImpl;
import com.sobow.shopping.services.UserService;
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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    
    private final Mapper<User, SelfCreateUserRequest> selfCreateRequestMapper;
    private final Mapper<User, UserResponse> userResponseMapper;
    
    @Transactional
    @Override
    public UserResponse mapToUserResponse(User user) {
        return userResponseMapper.mapToDto(user);
    }
    
    @Transactional
    @Override
    public User selfCreate(SelfCreateUserRequest createRequest) {
        User user = selfCreateRequestMapper.mapToEntity(createRequest);
        assertNewEmailAvailable(createRequest.email(), null);
        user.addAuthorityAndLink(new UserAuthority("USER"));
        user.setPassword(passwordEncoder.encode(createRequest.password().value()));
        return userRepository.save(user);
    }
    
    @Transactional
    @Override
    public User selfPartialUpdate(SelfUpdateUserRequest updateRequest) {
        Authentication authentication = getAuthentication();
        User existingUser = getAuthenticatedUser(authentication);
        existingUser.updateFrom(updateRequest);
        return existingUser;
    }
    
    @Transactional
    @Override
    public void selfUpdatePassword(SelfUpdatePasswordRequest updateRequest) {
        Authentication authentication = getAuthentication();
        User user = getAuthenticatedUser(authentication);
        assertPasswordMatch(updateRequest.oldPassword().value(), user.getPassword());
        
        user.setPassword(passwordEncoder.encode(updateRequest.newPassword().value()));
        
        updateSecurityContext(user, authentication.getAuthorities());
    }
    
    @Transactional
    @Override
    public void selfUpdateEmail(SelfUpdateEmailRequest updateRequest) {
        Authentication authentication = getAuthentication();
        User user = getAuthenticatedUser(authentication);
        assertPasswordMatch(updateRequest.oldPassword().value(), user.getPassword());
        
        assertNewEmailAvailable(updateRequest.newEmail(), user.getId());
        user.setEmail(updateRequest.newEmail());
        
        updateSecurityContext(user, authentication.getAuthorities());
    }
    
    @Transactional
    @Override
    public void selfDelete(SelfDeleteUserRequest deleteRequest) {
        Authentication authentication = getAuthentication();
        User user = getAuthenticatedUser(authentication);
        assertPasswordMatch(deleteRequest.oldPassword().value(), user.getPassword());
        userRepository.deleteById(user.getId());
    }
    
    
    private void assertNewEmailAvailable(String newEmail, @Nullable Long existingUserId) {
        boolean duplicate =
            (existingUserId == null && userRepository.existsByEmail(newEmail)) ||
                (existingUserId != null && userRepository.existsByEmailAndIdNot(newEmail, existingUserId));
        
        if (duplicate) throw new EmailAlreadyExistsException(newEmail);
    }
    
    private void assertPasswordMatch(String rawOldPassword, String encodedPassword) {
        if (!passwordEncoder.matches(rawOldPassword, encodedPassword)) {
            throw new InvalidOldPasswordException();
        }
    }
    
    private Authentication getAuthentication() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) throw new NoAuthenticationException();
        return auth;
    }
    
    private User getAuthenticatedUser(Authentication auth) {
        String email = auth.getName();
        return userRepository.findByEmail(email)
                             .orElseThrow(() -> new UsernameNotFoundException(
                                 "User with email: " + email + " not found"));
    }
    
    private void updateSecurityContext(User updatedUser, Collection<? extends GrantedAuthority> authorities) {
        UserDetails updatedPrincipal = new UserDetailsImpl(updatedUser);
        Authentication newAuth =
            new UsernamePasswordAuthenticationToken(updatedPrincipal, null, authorities);
        
        SecurityContextHolder.getContext().setAuthentication(newAuth);
    }
}
