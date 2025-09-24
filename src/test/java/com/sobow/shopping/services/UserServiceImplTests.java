package com.sobow.shopping.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.sobow.shopping.domain.user.User;
import com.sobow.shopping.domain.user.UserAddress;
import com.sobow.shopping.domain.user.UserProfile;
import com.sobow.shopping.domain.user.requests.self.SelfCreateUserRequest;
import com.sobow.shopping.domain.user.requests.self.SelfDeleteUserRequest;
import com.sobow.shopping.domain.user.requests.self.SelfUpdateEmailRequest;
import com.sobow.shopping.domain.user.requests.self.SelfUpdatePasswordRequest;
import com.sobow.shopping.domain.user.requests.self.SelfUpdateUserRequest;
import com.sobow.shopping.domain.user.requests.shared.PasswordDto;
import com.sobow.shopping.exceptions.EmailAlreadyExistsException;
import com.sobow.shopping.exceptions.InvalidOldPasswordException;
import com.sobow.shopping.exceptions.NoAuthenticationException;
import com.sobow.shopping.mappers.Mapper;
import com.sobow.shopping.repositories.UserRepository;
import com.sobow.shopping.security.UserDetailsImpl;
import com.sobow.shopping.services.Impl.UserServiceImpl;
import com.sobow.shopping.utils.TestFixtures;
import java.util.Optional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTests {
    
    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private Mapper<User, SelfCreateUserRequest> selfCreateRequestMapper;
    @InjectMocks
    private UserServiceImpl underTest;
    
    private final TestFixtures fixtures = new TestFixtures();
    
    @BeforeEach
    void setupContext() {
        User user = fixtures.userEntity();
        var principal = new UserDetailsImpl(user);
        Authentication auth = new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());
        SecurityContext ctx = SecurityContextHolder.createEmptyContext();
        ctx.setAuthentication(auth);
        SecurityContextHolder.setContext(ctx);
    }
    
    @AfterEach
    void tearDownContext() {
        SecurityContextHolder.clearContext();
    }
    
    @Nested
    @DisplayName("selfCreate")
    class selfCreate {
        
        @Test
        public void selfCreate_should_CreateNewUser_when_ValidInput() {
            // Given
            SelfCreateUserRequest createRequest = fixtures.selfCreateUserRequest();
            User user = fixtures.userEntity();
            
            when(selfCreateRequestMapper.mapToEntity(createRequest)).thenReturn(user);
            when(userRepository.existsByEmail(fixtures.email())).thenReturn(false);
            when(passwordEncoder.encode(createRequest.password().value())).thenReturn(fixtures.encodedPassword());
            when(userRepository.save(user)).thenReturn(user);
            
            // When
            User result = underTest.selfCreate(createRequest);
            
            // Then
            // Assert: request mapped to entity
            verify(selfCreateRequestMapper).mapToEntity(createRequest);
            
            // Assert: email uniqueness was checked with the new email
            verify(userRepository).existsByEmail(createRequest.email());
            
            // Assert: password was encoded and set on the entity
            verify(passwordEncoder).encode(createRequest.password().value());
            assertThat(user.getPassword()).isEqualTo(fixtures.encodedPassword());
            
            // Assert: entity was persisted
            verify(userRepository).save(user);
            
            // Assert: service returns the same (persisted) entity instance
            assertThat(user).isSameAs(result);
            
            // Assert: user has USER authority assigned
            assertThat(user.getAuthorities().stream().findFirst().get().getValue()).isEqualTo("ROLE_USER");
        }
        
        @Test
        public void selfCreate_should_ThrowAlreadyExists_when_EmailAlreadyUsed() {
            // Given
            SelfCreateUserRequest createRequest = fixtures.selfCreateUserRequest();
            User user = fixtures.userEntity();
            
            when(selfCreateRequestMapper.mapToEntity(createRequest)).thenReturn(user);
            when(userRepository.existsByEmail(fixtures.email())).thenReturn(true);
            
            // When & Then
            assertThrows(EmailAlreadyExistsException.class, () -> underTest.selfCreate(createRequest));
            
            // Assert: request was mapped to entity
            verify(selfCreateRequestMapper).mapToEntity(createRequest);
            
            // Assert: uniqueness check performed with the new email
            verify(userRepository).existsByEmail(createRequest.email());
            
            // Assert: no password encoding
            verifyNoInteractions(passwordEncoder);
        }
    }
    
    @Nested
    @DisplayName("selfPartialUpdate")
    class selfPartialUpdate {
        
        @Test
        public void selfPartialUpdate_should_UpdateAuthenticatedUser_when_ValidRequest() {
            // Given
            SelfUpdateUserRequest updateRequest = fixtures.selfUpdateUserRequest();
            User user = fixtures.userEntity();
            UserProfile userProfile = fixtures.userProfileEntity();
            UserAddress userAddress = fixtures.userAddressEntity();
            user.setProfileAndLink(userProfile);
            userProfile.setAddressAndLink(userAddress);
            
            when(userRepository.findByEmail(fixtures.email())).thenReturn(Optional.of(user));
            
            // When
            User result = underTest.selfPartialUpdate(updateRequest);
            
            // Then
            // Assert: repo lookup was done with authenticated email
            verify(userRepository).findByEmail(fixtures.email());
            
            // Assert: service returned the same (updated) managed entity
            assertThat(result).isSameAs(user);
            
            // Assert: user profile fields were updated from request
            assertThat(user.getProfile().getFirstName()).isEqualTo(updateRequest.userProfile().firstName());
            assertThat(user.getProfile().getLastName()).isEqualTo(updateRequest.userProfile().lastName());
            
            // Assert: address fields were updated from request
            assertThat(user.getProfile().getAddress().getCityName()).isEqualTo(updateRequest.userProfile().userAddress().cityName());
            assertThat(user.getProfile().getAddress().getStreetName()).isEqualTo(updateRequest.userProfile().userAddress().streetName());
            assertThat(user.getProfile().getAddress().getStreetNumber()).isEqualTo(updateRequest.userProfile().userAddress().streetNumber());
            assertThat(user.getProfile().getAddress().getPostCode()).isEqualTo(updateRequest.userProfile().userAddress().postCode());
        }
        
        @Test
        public void selfPartialUpdate_should_ThrowNoAuthentication_when_SecurityContextIsEmpty() {
            // Given
            SecurityContextHolder.clearContext();
            SelfUpdateUserRequest updateRequest = fixtures.selfUpdateUserRequest();
            
            // When & Then
            assertThrows(NoAuthenticationException.class, () -> underTest.selfPartialUpdate(updateRequest));
            
            // Assert: no repository access happened
            verify(userRepository, never()).findByEmail(anyString());
            
            // Assert: security context is still empty
            assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
        }
    }
    
    @Nested
    @DisplayName("selfUpdatePassword")
    class selfUpdatePassword {
        
        @Test
        public void selfUpdatePassword_should_UpdatePassword_and_RefreshSecurityContext_when_OldPasswordMatches() {
            // Given
            User user = fixtures.userEntity();
            String beforeHash = user.getPassword();
            
            when(userRepository.findByEmail(fixtures.email())).thenReturn(Optional.of(user));
            when(passwordEncoder.matches(fixtures.password(), user.getPassword())).thenReturn(true);
            when(passwordEncoder.encode(fixtures.newPassword())).thenReturn(fixtures.encodedPassword());
            
            // When
            underTest.selfUpdatePassword(new SelfUpdatePasswordRequest(new PasswordDto(fixtures.password()),
                                                                       new PasswordDto(fixtures.newPassword())));
            
            // Then
            // Assert: user loaded and old password verified
            verify(userRepository).findByEmail(fixtures.email());
            verify(passwordEncoder).matches(fixtures.password(), beforeHash);
            
            // Assert: new password encoded and set
            verify(passwordEncoder).encode(fixtures.newPassword());
            assertThat(user.getPassword()).isEqualTo(fixtures.encodedPassword());
        }
        
        @Test
        public void selfUpdatePassword_should_ThrowInvalidOldPassword_when_OldPasswordDoesNotMatch() {
            // Given
            User user = fixtures.userEntity();
            String beforeHash = user.getPassword();
            
            when(userRepository.findByEmail(fixtures.email())).thenReturn(Optional.of(user));
            when(passwordEncoder.matches(fixtures.password(), user.getPassword())).thenReturn(false);
            
            // When & Then
            assertThrows(InvalidOldPasswordException.class,
                         () -> underTest.selfUpdatePassword(new SelfUpdatePasswordRequest(new PasswordDto(fixtures.password()),
                                                                                          new PasswordDto(fixtures.newPassword()))));
            
            // Assert: old password was checked
            verify(passwordEncoder).matches(fixtures.password(), user.getPassword());
            
            // Assert: no encoding
            verify(passwordEncoder, never()).encode(anyString());
            
            // Assert: user's password value did not change
            assertThat(user.getPassword()).isEqualTo(beforeHash);
        }
        
        @Test
        public void selfUpdatePassword_should_ThrowNoAuthentication_when_SecurityContextIsEmpty() {
            // Given
            SecurityContextHolder.clearContext();
            
            // When & Then
            assertThrows(NoAuthenticationException.class,
                         () -> underTest.selfUpdatePassword(new SelfUpdatePasswordRequest(new PasswordDto(fixtures.password()),
                                                                                          new PasswordDto(fixtures.newPassword()))));
            
            // Assert: nothing was touched
            verify(userRepository, never()).findByEmail(anyString());
            verify(passwordEncoder, never()).matches(anyString(), anyString());
            verify(passwordEncoder, never()).encode(anyString());
            
            // Assert: security context is still empty
            assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
        }
    }
    
    @Nested
    @DisplayName("selfUpdateEmail")
    class selfUpdateEmail {
        
        @Test
        public void selfUpdateEmail_should_UpdateEmail_and_RefreshSecurityContext_when_Valid() {
            // Given
            User user = fixtures.userEntity();
            ReflectionTestUtils.setField(user, "id", fixtures.userId());
            
            String emailBefore = user.getEmail();
            
            when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
            when(passwordEncoder.matches(fixtures.password(), user.getPassword())).thenReturn(true);
            when(userRepository.existsByEmailAndIdNot(fixtures.newEmail(), fixtures.userId())).thenReturn(false);
            
            // When
            
            underTest.selfUpdateEmail(new SelfUpdateEmailRequest(new PasswordDto(fixtures.password()), fixtures.newEmail()));
            
            // Then
            // Assert: loaded by current email
            verify(userRepository).findByEmail(emailBefore);
            
            // Assert: old password checked against the OLD stored hash
            verify(passwordEncoder).matches(fixtures.password(), user.getPassword());
            
            // Assert: new email uniqueness checked against other users
            verify(userRepository).existsByEmailAndIdNot(fixtures.newEmail(), fixtures.userId());
            
            // Assert: email actually updated on the entity
            assertThat(user.getEmail()).isEqualTo(fixtures.newEmail());
        }
        
        @Test
        public void selfUpdateEmail_should_ThrowInvalidOldPassword_when_OldPasswordDoesNotMatch() {
            // Given
            User user = fixtures.userEntity();
            when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
            when(passwordEncoder.matches(fixtures.password(), user.getPassword())).thenReturn(false);
            
            // When & Then
            assertThrows(InvalidOldPasswordException.class,
                         () -> underTest.selfUpdateEmail(new SelfUpdateEmailRequest(new PasswordDto(fixtures.password()),
                                                                                    fixtures.newEmail())));
            
            // Assert: looked up current user and old password was checked
            verify(userRepository).findByEmail(user.getEmail());
            
            // Assert: old password checked against the OLD stored hash
            verify(passwordEncoder).matches(fixtures.password(), user.getPassword());
            
            // Assert: NO uniqueness check
            verify(userRepository, never()).existsByEmailAndIdNot(anyString(), anyLong());
            
            // Assert: email unchanged
            assertThat(user.getEmail()).isNotEqualTo(fixtures.newEmail());
        }
        
        @Test
        public void selfUpdateEmail_should_ThrowAlreadyExists_when_NewEmailTakenByAnotherUser() {
            // Given
            User user = fixtures.userEntity();
            String emailBefore = user.getEmail();
            
            ReflectionTestUtils.setField(user, "id", fixtures.userId());
            
            when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
            when(passwordEncoder.matches(fixtures.password(), user.getPassword())).thenReturn(true);
            when(userRepository.existsByEmailAndIdNot(fixtures.newEmail(), fixtures.userId())).thenReturn(true);
            
            // When & Then
            assertThrows(EmailAlreadyExistsException.class,
                         () -> underTest.selfUpdateEmail(new SelfUpdateEmailRequest(new PasswordDto(fixtures.password()),
                                                                                    fixtures.newEmail())));
            
            // Assert: user loaded by current email
            verify(userRepository).findByEmail(emailBefore);
            
            // Assert: old password verified against OLD stored hash
            verify(passwordEncoder).matches(fixtures.password(), user.getPassword());
            
            // Assert: uniqueness checked for new email against other users
            verify(userRepository).existsByEmailAndIdNot(fixtures.newEmail(), fixtures.userId());
            
            // Assert: no state changes
            assertThat(user.getEmail()).isEqualTo(emailBefore);
        }
        
        @Test
        public void selfUpdateEmail_should_ThrowNoAuthentication_when_SecurityContextIsEmpty() {
            // Given
            SecurityContextHolder.clearContext();
            
            // When & Then
            assertThrows(NoAuthenticationException.class,
                         () -> underTest.selfUpdateEmail(new SelfUpdateEmailRequest(new PasswordDto(fixtures.password()),
                                                                                    fixtures.newEmail())));
            
            // Assert: nothing hit the collaborators
            verify(userRepository, never()).findByEmail(anyString());
            verify(passwordEncoder, never()).matches(anyString(), anyString());
            verify(userRepository, never()).existsByEmailAndIdNot(anyString(), anyLong());
        }
    }
    
    @Nested
    @DisplayName("selfDelete")
    class selfDelete {
        
        @Test
        public void selfDelete_should_DeleteUser_when_EmailExists() {
            // Given
            User user = fixtures.userEntity();
            
            when(userRepository.findByEmail(fixtures.email())).thenReturn(Optional.of(user));
            when(passwordEncoder.matches(fixtures.password(), user.getPassword())).thenReturn(true);
            
            // When
            underTest.selfDelete(new SelfDeleteUserRequest(new PasswordDto(fixtures.password())));
            
            // Then
            // Assert: looked up by email
            verify(userRepository).findByEmail(fixtures.email());
            
            // Assert: entity delete invoked
            verify(userRepository).deleteById(user.getId());
        }
        
        @Test
        public void selfDelete_should_ThrowInvalidOldPassword_when_PasswordDoesNotMatch() {
            // Given
            User user = fixtures.userEntity();
            
            when(userRepository.findByEmail(fixtures.email())).thenReturn(Optional.of(user));
            when(passwordEncoder.matches(fixtures.password(), user.getPassword())).thenReturn(false);
            
            // When & Then
            assertThrows(InvalidOldPasswordException.class,
                         () -> underTest.selfDelete(new SelfDeleteUserRequest(new PasswordDto(fixtures.password()))));
            
            // Assert: looked up by email
            verify(userRepository).findByEmail(fixtures.email());
            
            // Assert: entity NOT deleted
            verify(userRepository, never()).deleteById(user.getId());
        }
    }
}