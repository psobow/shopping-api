package com.sobow.shopping.services.user.Impl;

import com.sobow.shopping.controllers.user.requests.admin.AdminUserCreateRequest;
import com.sobow.shopping.controllers.user.responses.UserResponse;
import com.sobow.shopping.domain.user.User;
import com.sobow.shopping.domain.user.UserRepository;
import com.sobow.shopping.mappers.user.requests.AdminUserCreateRequestMapper;
import com.sobow.shopping.mappers.user.responses.UserResponseMapper;
import com.sobow.shopping.services.user.AdminService;
import com.sobow.shopping.services.user.CurrentUserService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class AdminServiceImpl implements AdminService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final CurrentUserService currentUserService;
    
    private final AdminUserCreateRequestMapper adminUserCreateRequestMapper;
    
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
    public User adminCreate(AdminUserCreateRequest createRequest) {
        User user = adminUserCreateRequestMapper.mapToEntity(createRequest);
        currentUserService.assertNewEmailAvailable(createRequest.email(), null);
        user.setPassword(passwordEncoder.encode(createRequest.password().value()));
        return userRepository.save(user);
    }
    
    @Override
    public User findByEmailWithAuthorities(String email) {
        return userRepository.findByEmailWithAuthorities(email)
                             .orElseThrow(() -> new UsernameNotFoundException(
                                 "User with email: " + email + " not found"));
    }
    
    @Override
    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                             .orElseThrow(() -> new UsernameNotFoundException(
                                 "User with email: " + email + " not found"));
    }
    
    @Override
    public boolean userExistsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }
    
}
