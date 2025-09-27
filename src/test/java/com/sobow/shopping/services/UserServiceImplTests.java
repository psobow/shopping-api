package com.sobow.shopping.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.sobow.shopping.controllers.user.requests.PasswordRequest;
import com.sobow.shopping.controllers.user.requests.self.SelfEmailUpdateRequest;
import com.sobow.shopping.controllers.user.requests.self.SelfPasswordUpdateRequest;
import com.sobow.shopping.controllers.user.requests.self.SelfUserCreateRequest;
import com.sobow.shopping.controllers.user.requests.self.SelfUserDeleteRequest;
import com.sobow.shopping.controllers.user.requests.self.SelfUserPartialUpdateRequest;
import com.sobow.shopping.domain.user.User;
import com.sobow.shopping.domain.user.UserAddress;
import com.sobow.shopping.domain.user.UserProfile;
import com.sobow.shopping.domain.user.UserRepository;
import com.sobow.shopping.exceptions.EmailAlreadyExistsException;
import com.sobow.shopping.exceptions.InvalidOldPasswordException;
import com.sobow.shopping.exceptions.NoAuthenticationException;
import com.sobow.shopping.mappers.user.requests.SelfUserCreateRequestMapper;
import com.sobow.shopping.security.UserDetailsImpl;
import com.sobow.shopping.services.user.CurrentUserService;
import com.sobow.shopping.services.user.Impl.UserServiceImpl;
import com.sobow.shopping.utils.TestFixtures;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
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
    private CurrentUserService currentUserService;
    
    @Mock
    private SelfUserCreateRequestMapper selfUserCreateRequestMapper;
    
    @InjectMocks
    private UserServiceImpl underTest;
    
    private final TestFixtures fixtures = new TestFixtures();

//    @BeforeEach
//    void setupContext() {
//        User user = fixtures.userEntity();
//        var principal = new UserDetailsImpl(user);
//        Authentication auth = new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());
//        SecurityContext ctx = SecurityContextHolder.createEmptyContext();
//        ctx.setAuthentication(auth);
//        SecurityContextHolder.setContext(ctx);
//    }
//
//    @AfterEach
//    void tearDownContext() {
//        SecurityContextHolder.clearContext();
//    }
    
    @Nested
    @DisplayName("selfCreate")
    class selfCreate {
        
        @Test
        public void selfCreate_should_CreateNewUser_when_ValidInput() {
            // Given
            SelfUserCreateRequest createRequest = fixtures.selfCreateUserRequest();
            User user = fixtures.userEntity();
            
            when(selfUserCreateRequestMapper.mapToEntity(createRequest)).thenReturn(user);
            when(passwordEncoder.encode(createRequest.password().value())).thenReturn(fixtures.encodedPassword());
            when(userRepository.save(user)).thenReturn(user);
            
            // When
            User result = underTest.selfCreate(createRequest);
            
            // Then
            // Assert: request mapped to entity
            verify(selfUserCreateRequestMapper).mapToEntity(createRequest);
            
            // Assert: email uniqueness was checked with the new email
            verify(currentUserService).assertNewEmailAvailable(createRequest.email(), null);
            
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
            SelfUserCreateRequest createRequest = fixtures.selfCreateUserRequest();
            User user = fixtures.userEntity();
            
            when(selfUserCreateRequestMapper.mapToEntity(createRequest)).thenReturn(user);
            doThrow(new EmailAlreadyExistsException("email"))
                .when(currentUserService)
                .assertNewEmailAvailable(createRequest.email(), null);
            
            // When & Then
            assertThrows(EmailAlreadyExistsException.class, () -> underTest.selfCreate(createRequest));
            
            // Assert: request was mapped to entity
            verify(selfUserCreateRequestMapper).mapToEntity(createRequest);
            
            // Assert: uniqueness check performed with the new email
            verify(currentUserService).assertNewEmailAvailable(createRequest.email(), null);
            
            // Assert: no password encoding
            verifyNoInteractions(passwordEncoder);
            
            // Assert: entity was NOT persisted
            verify(userRepository, never()).save(user);
        }
    }
    
    @Nested
    @DisplayName("selfPartialUpdate")
    class selfPartialUpdate {
        
        @Test
        public void selfPartialUpdate_should_UpdateAuthenticatedUser_when_ValidRequest() {
            // Given
            SelfUserPartialUpdateRequest updateRequest = fixtures.selfUpdateUserRequest();
            User user = fixtures.userEntity();
            UserProfile userProfile = fixtures.userProfileEntity();
            UserAddress userAddress = fixtures.userAddressEntity();
            user.setProfileAndLink(userProfile);
            userProfile.setAddressAndLink(userAddress);
            
            when(currentUserService.getAuthenticatedUser(any())).thenReturn(user);
            
            // When
            User result = underTest.selfPartialUpdate(updateRequest);
            
            // Then
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
            SelfUserPartialUpdateRequest updateRequest = fixtures.selfUpdateUserRequest();
            
            doThrow(new NoAuthenticationException())
                .when(currentUserService)
                .getAuthentication();
            
            // When & Then
            assertThrows(NoAuthenticationException.class, () -> underTest.selfPartialUpdate(updateRequest));
            
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
            
            var principal = new UserDetailsImpl(user);
            Authentication auth = new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());
            
            when(currentUserService.getAuthenticatedUser(any())).thenReturn(user);
            when(passwordEncoder.encode(fixtures.newPassword())).thenReturn(fixtures.encodedPassword());
            when(currentUserService.getAuthentication()).thenReturn(auth);
            
            // When
            underTest.selfUpdatePassword(new SelfPasswordUpdateRequest(new PasswordRequest(fixtures.password()),
                                                                       new PasswordRequest(fixtures.newPassword())));
            
            // Then
            // Assert: user loaded and old password verified
            verify(currentUserService).assertPasswordMatch(fixtures.password(), beforeHash);
            
            // Assert: new password encoded and set
            verify(passwordEncoder).encode(fixtures.newPassword());
            assertThat(user.getPassword()).isEqualTo(fixtures.encodedPassword());
        }
        
        @Test
        public void selfUpdatePassword_should_ThrowInvalidOldPassword_when_OldPasswordDoesNotMatch() {
            // Given
            User user = fixtures.userEntity();
            String beforeHash = user.getPassword();
            
            when(currentUserService.getAuthenticatedUser(any())).thenReturn(user);
            
            doThrow(new InvalidOldPasswordException())
                .when(currentUserService)
                .assertPasswordMatch(fixtures.password(), user.getPassword());
            
            // When & Then
            assertThrows(InvalidOldPasswordException.class,
                         () -> underTest.selfUpdatePassword(new SelfPasswordUpdateRequest(new PasswordRequest(fixtures.password()),
                                                                                          new PasswordRequest(fixtures.newPassword()))));
            
            // Assert: no encoding
            verify(passwordEncoder, never()).encode(anyString());
            
            // Assert: user's password value did not change
            assertThat(user.getPassword()).isEqualTo(beforeHash);
        }
        
        @Test
        public void selfUpdatePassword_should_ThrowNoAuthentication_when_SecurityContextIsEmpty() {
            // Given
            SecurityContextHolder.clearContext();
            
            doThrow(new NoAuthenticationException())
                .when(currentUserService)
                .getAuthentication();
            
            // When & Then
            assertThrows(NoAuthenticationException.class,
                         () -> underTest.selfUpdatePassword(new SelfPasswordUpdateRequest(new PasswordRequest(fixtures.password()),
                                                                                          new PasswordRequest(fixtures.newPassword()))));
            
            
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
            String beforeHash = user.getPassword();
            ReflectionTestUtils.setField(user, "id", fixtures.userId());
            
            var principal = new UserDetailsImpl(user);
            Authentication auth = new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());
            
            when(currentUserService.getAuthenticatedUser(any())).thenReturn(user);
            when(currentUserService.getAuthentication()).thenReturn(auth);
            
            // When
            underTest.selfUpdateEmail(new SelfEmailUpdateRequest(new PasswordRequest(fixtures.password()), fixtures.newEmail()));
            
            // Then
            // Assert: old password checked against the OLD stored hash
            verify(currentUserService).assertPasswordMatch(fixtures.password(), beforeHash);
            
            // Assert: new email uniqueness checked against other users
            verify(currentUserService).assertNewEmailAvailable(fixtures.newEmail(), fixtures.userId());
            
            // Assert: email actually updated on the entity
            assertThat(user.getEmail()).isEqualTo(fixtures.newEmail());
        }
        
        @Test
        public void selfUpdateEmail_should_ThrowInvalidOldPassword_when_OldPasswordDoesNotMatch() {
            // Given
            User user = fixtures.userEntity();
            when(currentUserService.getAuthenticatedUser(any())).thenReturn(user);
            
            doThrow(new InvalidOldPasswordException())
                .when(currentUserService)
                .assertPasswordMatch(fixtures.password(), user.getPassword());
            
            // When & Then
            assertThrows(InvalidOldPasswordException.class,
                         () -> underTest.selfUpdateEmail(new SelfEmailUpdateRequest(new PasswordRequest(fixtures.password()),
                                                                                    fixtures.newEmail())));
            
            // Assert: email unchanged
            assertThat(user.getEmail()).isNotEqualTo(fixtures.newEmail());
        }
        
        @Test
        public void selfUpdateEmail_should_ThrowAlreadyExists_when_NewEmailTakenByAnotherUser() {
            // Given
            User user = fixtures.userEntity();
            String emailBefore = user.getEmail();
            
            ReflectionTestUtils.setField(user, "id", fixtures.userId());
            
            when(currentUserService.getAuthenticatedUser(any())).thenReturn(user);
            
            doThrow(new EmailAlreadyExistsException("email"))
                .when(currentUserService)
                .assertNewEmailAvailable(fixtures.newEmail(), fixtures.userId());
            
            // When & Then
            assertThrows(EmailAlreadyExistsException.class,
                         () -> underTest.selfUpdateEmail(new SelfEmailUpdateRequest(new PasswordRequest(fixtures.password()),
                                                                                    fixtures.newEmail())));
            
            
            // Assert: old password verified against OLD stored hash
            verify(currentUserService).assertPasswordMatch(fixtures.password(), user.getPassword());
            
            // Assert: no state changes
            assertThat(user.getEmail()).isEqualTo(emailBefore);
        }
        
        @Test
        public void selfUpdateEmail_should_ThrowNoAuthentication_when_SecurityContextIsEmpty() {
            // Given
            SecurityContextHolder.clearContext();
            
            doThrow(new NoAuthenticationException())
                .when(currentUserService)
                .getAuthentication();
            
            // When & Then
            assertThrows(NoAuthenticationException.class,
                         () -> underTest.selfUpdateEmail(new SelfEmailUpdateRequest(new PasswordRequest(fixtures.password()),
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
            
            when(currentUserService.getAuthenticatedUser(any())).thenReturn(user);
            
            // When
            underTest.selfDelete(new SelfUserDeleteRequest(new PasswordRequest(fixtures.password())));
            
            // Then
            // Assert: password check
            verify(currentUserService).assertPasswordMatch(fixtures.password(), user.getPassword());
            
            // Assert: entity delete invoked
            verify(userRepository).deleteById(user.getId());
        }
        
        @Test
        public void selfDelete_should_ThrowInvalidOldPassword_when_PasswordDoesNotMatch() {
            // Given
            User user = fixtures.userEntity();
            
            when(currentUserService.getAuthenticatedUser(any())).thenReturn(user);
            
            doThrow(new InvalidOldPasswordException())
                .when(currentUserService)
                .assertPasswordMatch(fixtures.password(), user.getPassword());
            
            // When & Then
            assertThrows(InvalidOldPasswordException.class,
                         () -> underTest.selfDelete(new SelfUserDeleteRequest(new PasswordRequest(fixtures.password()))));
            
            
            // Assert: entity NOT deleted
            verify(userRepository, never()).deleteById(user.getId());
        }
    }
}