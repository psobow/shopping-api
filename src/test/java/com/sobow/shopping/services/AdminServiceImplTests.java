package com.sobow.shopping.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.sobow.shopping.controllers.user.requests.admin.AdminUserCreateRequest;
import com.sobow.shopping.domain.user.User;
import com.sobow.shopping.domain.user.UserRepository;
import com.sobow.shopping.exceptions.EmailAlreadyExistsException;
import com.sobow.shopping.mappers.user.requests.AdminUserCreateRequestMapper;
import com.sobow.shopping.services.user.CurrentUserService;
import com.sobow.shopping.services.user.Impl.AdminServiceImpl;
import com.sobow.shopping.utils.TestFixtures;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
public class AdminServiceImplTests {
    
    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private AdminUserCreateRequestMapper adminUserCreateRequestMapper;
    @Mock
    private CurrentUserService currentUserService;
    
    @InjectMocks
    private AdminServiceImpl underTest;
    
    private final TestFixtures fixtures = new TestFixtures();
    
    @Nested
    @DisplayName("adminCreate")
    class adminCreate {
        
        @Test
        public void adminCreate_should_CreateNewUser_when_ValidInput() {
            // Given
            AdminUserCreateRequest createRequest = fixtures.adminCreateUserRequest();
            User user = fixtures.userEntity();
            
            when(adminUserCreateRequestMapper.mapToEntity(createRequest)).thenReturn(user);
            when(passwordEncoder.encode(createRequest.password().value())).thenReturn(fixtures.encodedPassword());
            when(userRepository.save(user)).thenReturn(user);
            
            // When
            User result = underTest.adminCreate(createRequest);
            
            // Then
            // Assert: request mapped to entity
            verify(adminUserCreateRequestMapper).mapToEntity(createRequest);
            
            // Assert: email uniqueness was checked with the new email
            verify(currentUserService).assertNewEmailAvailable(createRequest.email(), null);
            
            // Assert: password was encoded and set on the entity
            verify(passwordEncoder).encode(createRequest.password().value());
            assertThat(user.getPassword()).isEqualTo(fixtures.encodedPassword());
            
            // Assert: entity was persisted
            verify(userRepository).save(user);
            
            // Assert: service returns the same (persisted) entity instance
            assertThat(user).isSameAs(result);
        }
        
        @Test
        public void adminCreate_should_ThrowAlreadyExists_when_EmailAlreadyUsed() {
            // Given
            AdminUserCreateRequest createRequest = fixtures.adminCreateUserRequest();
            User user = fixtures.userEntity();
            
            when(adminUserCreateRequestMapper.mapToEntity(createRequest)).thenReturn(user);
            doThrow(new EmailAlreadyExistsException("email"))
                .when(currentUserService)
                .assertNewEmailAvailable(createRequest.email(), null);
            
            // When & Then
            assertThrows(EmailAlreadyExistsException.class, () -> underTest.adminCreate(createRequest));
            
            // Assert: request was mapped to entity
            verify(adminUserCreateRequestMapper).mapToEntity(createRequest);
            
            // Assert: uniqueness check performed with the new email
            verify(currentUserService).assertNewEmailAvailable(createRequest.email(), null);
            
            // Assert: no password encoding
            verifyNoInteractions(passwordEncoder);
            
            // Assert: entity was NOT persisted
            verify(userRepository, never()).save(user);
        }
    }
}
