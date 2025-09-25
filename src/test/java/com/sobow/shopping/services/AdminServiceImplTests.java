package com.sobow.shopping.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.sobow.shopping.domain.user.User;
import com.sobow.shopping.domain.user.requests.admin.AdminUserCreateRequest;
import com.sobow.shopping.exceptions.EmailAlreadyExistsException;
import com.sobow.shopping.mappers.user.requests.AdminUserCreateRequestMapper;
import com.sobow.shopping.repositories.UserRepository;
import com.sobow.shopping.security.UserDetailsImpl;
import com.sobow.shopping.services.Impl.AdminServiceImpl;
import com.sobow.shopping.utils.TestFixtures;
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

@ExtendWith(MockitoExtension.class)
public class AdminServiceImplTests {
    
    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private AdminUserCreateRequestMapper adminUserCreateRequestMapper;
    
    @InjectMocks
    private AdminServiceImpl underTest;
    
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
    @DisplayName("adminCreate")
    class adminCreate {
        
        @Test
        public void adminCreate_should_CreateNewUser_when_ValidInput() {
            // Given
            AdminUserCreateRequest createRequest = fixtures.adminCreateUserRequest();
            User user = fixtures.userEntity();
            
            when(adminUserCreateRequestMapper.mapToEntity(createRequest)).thenReturn(user);
            when(userRepository.existsByEmail(fixtures.email())).thenReturn(false);
            when(passwordEncoder.encode(createRequest.password().value())).thenReturn(fixtures.encodedPassword());
            when(userRepository.save(user)).thenReturn(user);
            
            // When
            User result = underTest.adminCreate(createRequest);
            
            // Then
            // Assert: request mapped to entity
            verify(adminUserCreateRequestMapper).mapToEntity(createRequest);
            
            // Assert: email uniqueness was checked with the new email
            verify(userRepository).existsByEmail(createRequest.email());
            
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
            when(userRepository.existsByEmail(fixtures.email())).thenReturn(true);
            
            // When & Then
            assertThrows(EmailAlreadyExistsException.class, () -> underTest.adminCreate(createRequest));
            
            // Assert: request was mapped to entity
            verify(adminUserCreateRequestMapper).mapToEntity(createRequest);
            
            // Assert: uniqueness check performed with the new email
            verify(userRepository).existsByEmail(createRequest.email());
            
            // Assert: no password encoding
            verifyNoInteractions(passwordEncoder);
            
            // Assert: entity was NOT persisted
            verify(userRepository, never()).save(user);
        }
    }
}
