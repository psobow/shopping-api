package com.sobow.shopping.services.Impl;

import com.sobow.shopping.domain.user.User;
import com.sobow.shopping.domain.user.requests.UserCreateRequest;
import com.sobow.shopping.domain.user.requests.UserUpdateRequest;
import com.sobow.shopping.exceptions.EmailAlreadyExistsException;
import com.sobow.shopping.exceptions.InvalidOldPasswordException;
import com.sobow.shopping.exceptions.NoAuthenticationException;
import com.sobow.shopping.mappers.Mapper;
import com.sobow.shopping.repositories.UserRepository;
import com.sobow.shopping.security.UserDetailsImpl;
import com.sobow.shopping.services.UserManagementService;
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
public class UserManagementServiceImpl implements UserManagementService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final Mapper<User, UserCreateRequest> userCreateRequestMapper;
    
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
    
    @Transactional
    @Override
    public User create(UserCreateRequest createRequest) {
        User user = userCreateRequestMapper.mapToEntity(createRequest);
        assertNewEmailAvailable(createRequest.email(), null);
        user.setPassword(passwordEncoder.encode(createRequest.password()));
        return userRepository.save(user);
    }
    
    @Transactional
    @Override
    public User selfPartialUpdate(UserUpdateRequest updateRequest) {
        Authentication authentication = getCurrentAuthentication();
        User existingUser = getAuthenticatedUser(authentication);
        existingUser.updateFrom(updateRequest);
        return existingUser;
    }
    
    @Transactional
    @Override
    public void selfUpdatePassword(String oldPassword, String newPassword) {
        Authentication authentication = getCurrentAuthentication();
        User user = getAuthenticatedUser(authentication);
        assertPasswordMatch(oldPassword, user.getPassword());
        
        user.setPassword(passwordEncoder.encode(newPassword));
        
        updateSecurityContext(user, authentication.getAuthorities());
    }
    
    @Transactional
    @Override
    public void selfUpdateEmail(String oldPassword, String newEmail) {
        Authentication authentication = getCurrentAuthentication();
        User user = getAuthenticatedUser(authentication);
        assertPasswordMatch(oldPassword, user.getPassword());
        
        assertNewEmailAvailable(newEmail, user.getId());
        user.setEmail(newEmail);
        
        updateSecurityContext(user, authentication.getAuthorities());
    }
    
    @Transactional
    @Override
    public void selfDelete(String oldPassword) {
        Authentication authentication = getCurrentAuthentication();
        User user = getAuthenticatedUser(authentication);
        assertPasswordMatch(oldPassword, user.getPassword());
        userRepository.deleteById(user.getId());
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
    
    private void assertPasswordMatch(String rawOldPassword, String encodedPassword) {
        if (!passwordEncoder.matches(rawOldPassword, encodedPassword)) {
            throw new InvalidOldPasswordException();
        }
    }
    
    private Authentication getCurrentAuthentication() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) throw new NoAuthenticationException();
        return auth;
    }
    
    private User getAuthenticatedUser(Authentication auth) {
        String email = auth.getName();
        return findByEmail(email);
    }
    
    private void updateSecurityContext(User updatedUser, Collection<? extends GrantedAuthority> authorities) {
        UserDetails updatedPrincipal = new UserDetailsImpl(updatedUser);
        Authentication newAuth =
            new UsernamePasswordAuthenticationToken(updatedPrincipal, null, authorities);
        
        SecurityContextHolder.getContext().setAuthentication(newAuth);
    }
}
