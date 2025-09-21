package com.sobow.shopping.security;

import com.sobow.shopping.domain.user.User;
import com.sobow.shopping.exceptions.EmailAlreadyExistsException;
import com.sobow.shopping.exceptions.InvalidOldPasswordException;
import com.sobow.shopping.exceptions.NoAuthenticationException;
import com.sobow.shopping.repositories.AuthorityRepository;
import com.sobow.shopping.repositories.UserRepository;
import java.util.Collection;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
public class UserDetailsManagerImpl implements CustomUserDetailsManager {
    
    private final UserRepository userRepository;
    private final AuthorityRepository authorityRepository;
    private final PasswordEncoder passwordEncoder;
    
    @Transactional(readOnly = true)
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmailWithAuthorities(email)
                                  .orElseThrow(() -> new UsernameNotFoundException(
                                      "User with email: " + email + " not found"));
        return new UserDetailsImpl(user);
    }
    
    @Transactional
    @Override
    public void createUser(UserDetails userDetails) {
        // Extract data from userDetails
        UserDetailsImpl userDetailsImpl = (UserDetailsImpl) userDetails;
        User userToCreate = userDetailsImpl.getUser();
        
        // Build user + encode password
        User newUser = User.builder()
                           .email(userToCreate.getEmail())
                           .password(passwordEncoder.encode(userToCreate.getPassword()))
                           .build()
                           .withAuthorities(userDetailsImpl.getAuthorities())
                           .withProfileAndAddress(userToCreate.getProfile(), userToCreate.getProfile().getAddress());
        
        userRepository.save(newUser);
    }
    
    // THIS METHOD IS NOT FOR EMAIL or PASSWORD UPDATE
    @Transactional
    @Override
    public void updateUser(UserDetails userDetails) {
        UserDetailsImpl userDetailsImpl = (UserDetailsImpl) userDetails;
        User userToUpdate = userDetailsImpl.getUser();
        
        User existingUser = userRepository.findByEmail(userToUpdate.getEmail())
                                          .orElseThrow(() -> new UsernameNotFoundException(
                                              "User with email: " + userToUpdate.getEmail() + " not found"));
        
        existingUser.updateAuthoritiesFrom(userDetailsImpl.getAuthorities());
        existingUser.updateProfileFrom(userToUpdate.getProfile());
    }
    
    @Transactional
    @Override
    public void deleteUser(String email) {
        // Deleting in this way Honors JPA cascades and orphanRemoval on associations in the entity model
        // Using only derived query deleteByEmail would skip cascades and orphanRemoval
        userRepository.findByEmail(email).ifPresent(userRepository::delete);
    }
    
    @Transactional
    @Override
    public void changePassword(String oldPassword, String newPassword) {
        Authentication authentication = getCurrentAuthentication();
        User user = getAuthenticatedUser(authentication);
        assertPasswordMatch(oldPassword, user);
        
        // Encode and set the new password
        user.setPassword(passwordEncoder.encode(newPassword));
        
        updateSecurityContext(user, authentication.getAuthorities());
    }
    
    @Transactional
    @Override
    public void changeEmail(String oldPassword, String newEmail) {
        Authentication authentication = getCurrentAuthentication();
        User user = getAuthenticatedUser(authentication);
        assertPasswordMatch(oldPassword, user);
        
        assertNewEmailAvailable(newEmail);
        user.setEmail(newEmail);
        
        updateSecurityContext(user, authentication.getAuthorities());
    }
    
    @Override
    public boolean userExists(String email) {
        return userRepository.existsByEmail(email);
    }
    
    private Authentication getCurrentAuthentication() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) throw new NoAuthenticationException();
        return auth;
    }
    
    private User getAuthenticatedUser(Authentication auth) {
        String email = auth.getName();
        return userRepository.findByEmail(email)
                             .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }
    
    private void assertPasswordMatch(String rawOldPassword, User user) {
        if (!passwordEncoder.matches(rawOldPassword, user.getPassword())) {
            throw new InvalidOldPasswordException();
        }
    }
    
    private void assertNewEmailAvailable(String newEmail) {
        boolean taken = userRepository.existsByEmail(newEmail);
        if (taken) throw new EmailAlreadyExistsException(newEmail);
    }
    
    private void updateSecurityContext(User user, Collection<? extends GrantedAuthority> authorities) {
        UserDetails updatedPrincipal = new UserDetailsImpl(user);
        Authentication newAuth =
            new UsernamePasswordAuthenticationToken(updatedPrincipal, null, authorities);
        
        SecurityContextHolder.getContext().setAuthentication(newAuth);
    }
}
