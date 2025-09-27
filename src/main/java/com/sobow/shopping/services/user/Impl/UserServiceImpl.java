package com.sobow.shopping.services.user.Impl;

import com.sobow.shopping.controllers.user.requests.self.SelfEmailUpdateRequest;
import com.sobow.shopping.controllers.user.requests.self.SelfPasswordUpdateRequest;
import com.sobow.shopping.controllers.user.requests.self.SelfUserCreateRequest;
import com.sobow.shopping.controllers.user.requests.self.SelfUserDeleteRequest;
import com.sobow.shopping.controllers.user.requests.self.SelfUserPartialUpdateRequest;
import com.sobow.shopping.domain.user.User;
import com.sobow.shopping.domain.user.UserAuthority;
import com.sobow.shopping.domain.user.UserRepository;
import com.sobow.shopping.mappers.user.requests.SelfUserCreateRequestMapper;
import com.sobow.shopping.services.user.CurrentUserService;
import com.sobow.shopping.services.user.UserService;
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
        currentUserService.assertPasswordMatch(updateRequest.password().value(), user.getPassword());
        user.setPassword(passwordEncoder.encode(updateRequest.newPassword().value()));
        currentUserService.updateSecurityContext(user, authentication.getAuthorities());
    }
    
    @Transactional
    @Override
    public void selfUpdateEmail(SelfEmailUpdateRequest updateRequest) {
        Authentication authentication = currentUserService.getAuthentication();
        User user = currentUserService.getAuthenticatedUser(authentication);
        currentUserService.assertPasswordMatch(updateRequest.password().value(), user.getPassword());
        currentUserService.assertNewEmailAvailable(updateRequest.newEmail(), user.getId());
        user.setEmail(updateRequest.newEmail());
        currentUserService.updateSecurityContext(user, authentication.getAuthorities());
    }
    
    @Transactional
    @Override
    public void selfDelete(SelfUserDeleteRequest deleteRequest) {
        Authentication authentication = currentUserService.getAuthentication();
        User user = currentUserService.getAuthenticatedUser(authentication);
        currentUserService.assertPasswordMatch(deleteRequest.password().value(), user.getPassword());
        userRepository.deleteById(user.getId());
    }
}
