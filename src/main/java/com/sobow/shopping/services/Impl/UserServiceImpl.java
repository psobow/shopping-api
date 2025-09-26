package com.sobow.shopping.services.Impl;

import com.sobow.shopping.domain.user.User;
import com.sobow.shopping.domain.user.UserAuthority;
import com.sobow.shopping.domain.user.requests.self.SelfEmailUpdateRequest;
import com.sobow.shopping.domain.user.requests.self.SelfPasswordUpdateRequest;
import com.sobow.shopping.domain.user.requests.self.SelfUserCreateRequest;
import com.sobow.shopping.domain.user.requests.self.SelfUserDeleteRequest;
import com.sobow.shopping.domain.user.requests.self.SelfUserPartialUpdateRequest;
import com.sobow.shopping.domain.user.responses.UserResponse;
import com.sobow.shopping.mappers.user.requests.SelfUserCreateRequestMapper;
import com.sobow.shopping.mappers.user.responses.UserResponseMapper;
import com.sobow.shopping.repositories.UserRepository;
import com.sobow.shopping.services.CurrentUserService;
import com.sobow.shopping.services.UserService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final CurrentUserService currentUserService;
    
    private final SelfUserCreateRequestMapper selfUserCreateRequestMapper;
    private final UserResponseMapper userResponseMapper;
    
    @Transactional
    @Override
    public UserResponse mapToUserResponse(long userId) {
        User user = userRepository.findById(userId)
                                  .orElseThrow(() -> new EntityNotFoundException("User with id" + userId + " not found"));
        return userResponseMapper.mapToDto(user);
    }
    
    @Transactional
    @Override
    public User selfCreate(SelfUserCreateRequest createRequest) {
        User user = selfUserCreateRequestMapper.mapToEntity(createRequest);
        currentUserService.assertNewEmailAvailable(createRequest.email(), null);
        user.addAuthorityAndLink(new UserAuthority("USER"));
        user.setPassword(passwordEncoder.encode(createRequest.password().value()));
        return userRepository.save(user);
    }
    
    @Transactional
    @Override
    public User selfPartialUpdate(SelfUserPartialUpdateRequest updateRequest) {
        Authentication authentication = currentUserService.getAuthentication();
        User existingUser = currentUserService.getAuthenticatedUser(authentication);
        existingUser.updateFrom(updateRequest);
        return existingUser;
    }
    
    @Transactional
    @Override
    public void selfUpdatePassword(SelfPasswordUpdateRequest updateRequest) {
        Authentication authentication = currentUserService.getAuthentication();
        User user = currentUserService.getAuthenticatedUser(authentication);
        currentUserService.assertPasswordMatch(updateRequest.oldPassword().value(), user.getPassword());
        
        user.setPassword(passwordEncoder.encode(updateRequest.newPassword().value()));
        
        currentUserService.updateSecurityContext(user, authentication.getAuthorities());
    }
    
    @Transactional
    @Override
    public void selfUpdateEmail(SelfEmailUpdateRequest updateRequest) {
        Authentication authentication = currentUserService.getAuthentication();
        User user = currentUserService.getAuthenticatedUser(authentication);
        currentUserService.assertPasswordMatch(updateRequest.oldPassword().value(), user.getPassword());
        
        currentUserService.assertNewEmailAvailable(updateRequest.newEmail(), user.getId());
        user.setEmail(updateRequest.newEmail());
        
        currentUserService.updateSecurityContext(user, authentication.getAuthorities());
    }
    
    @Transactional
    @Override
    public void selfDelete(SelfUserDeleteRequest deleteRequest) {
        Authentication authentication = currentUserService.getAuthentication();
        User user = currentUserService.getAuthenticatedUser(authentication);
        currentUserService.assertPasswordMatch(deleteRequest.oldPassword().value(), user.getPassword());
        userRepository.deleteById(user.getId());
    }
}
