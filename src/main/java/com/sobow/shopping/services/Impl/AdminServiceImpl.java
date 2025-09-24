package com.sobow.shopping.services.Impl;

import com.sobow.shopping.domain.user.User;
import com.sobow.shopping.domain.user.requests.admin.AdminCreateUserRequest;
import com.sobow.shopping.exceptions.EmailAlreadyExistsException;
import com.sobow.shopping.mappers.Mapper;
import com.sobow.shopping.repositories.UserRepository;
import com.sobow.shopping.services.AdminService;
import jakarta.annotation.Nullable;
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
    
    private final Mapper<User, AdminCreateUserRequest> adminCreateRequestMapper;
    
    @Transactional
    @Override
    public User adminCreate(AdminCreateUserRequest createRequest) {
        User user = adminCreateRequestMapper.mapToEntity(createRequest);
        assertNewEmailAvailable(createRequest.email(), null);
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
    
    private void assertNewEmailAvailable(String newEmail, @Nullable Long existingUserId) {
        boolean duplicate =
            (existingUserId == null && userRepository.existsByEmail(newEmail)) ||
                (existingUserId != null && userRepository.existsByEmailAndIdNot(newEmail, existingUserId));
        
        if (duplicate) throw new EmailAlreadyExistsException(newEmail);
    }
}
