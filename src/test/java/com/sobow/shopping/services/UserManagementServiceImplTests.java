package com.sobow.shopping.services;

import static org.junit.jupiter.api.Assertions.fail;

import com.sobow.shopping.domain.user.User;
import com.sobow.shopping.domain.user.dto.UserCreateRequest;
import com.sobow.shopping.mappers.Mapper;
import com.sobow.shopping.repositories.UserRepository;
import com.sobow.shopping.services.Impl.UserManagementServiceImpl;
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
class UserManagementServiceImplTests {
    
    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private Mapper<User, UserCreateRequest> userCreateRequestMapper;
    @InjectMocks
    private UserManagementServiceImpl underTest;
    
    private final TestFixtures fixtures = new TestFixtures();
    
    @Nested
    @DisplayName("findBy")
    class findBy {
        
        @Test
        public void findByEmailWithAuthorities_should_ReturnUser_when_EmailExists() {
            fail("implement me");
        }
        
        @Test
        public void findByEmailWithAuthorities_should_ThrowNotFound_when_EmailDoesNotExist() {
            fail("implement me");
        }
        
        @Test
        public void findByEmail_should_ReturnUser_when_EmailExists() {
            fail("implement me");
        }
        
        @Test
        public void findByEmail_should_ThrowNotFound_when_EmailDoesNotExist() {
            fail("implement me");
        }
    }
    
    @Nested
    @DisplayName("create")
    class create {
        
        @Test
        public void create_should_CreateNewUser_when_ValidInput() {
            fail("implement me");
        }
        
        @Test
        public void create_should_ThrowAlreadyExists_when_EmailAlreadyUsed() {
            fail("implement me");
        }
    }
    
    @Nested
    @DisplayName("partialUpdate")
    class partialUpdate {
        
        @Test
        public void partialUpdate_should_UpdateAuthenticatedUser_when_ValidRequest() {
            fail("implement me");
        }
        
        @Test
        public void partialUpdate_should_ThrowNoAuthentication_when_SecurityContextIsEmpty() {
            fail("implement me");
        }
    }
    
    @Nested
    @DisplayName("updatePassword")
    class updatePassword {
        
        @Test
        public void updatePassword_should_UpdatePassword_and_RefreshSecurityContext_when_OldPasswordMatches() {
            fail("implement me");
        }
        
        @Test
        public void updatePassword_should_ThrowInvalidOldPassword_when_OldPasswordDoesNotMatch() {
            fail("implement me");
        }
        
        @Test
        public void updatePassword_should_ThrowNoAuthentication_when_SecurityContextIsEmpty() {
            fail("implement me");
        }
    }
    
    @Nested
    @DisplayName("updateEmail")
    class updateEmail {
        
        @Test
        public void updateEmail_should_UpdateEmail_and_RefreshSecurityContext_when_Valid() {
            fail("implement me");
        }
        
        @Test
        public void updateEmail_should_ThrowInvalidOldPassword_when_OldPasswordDoesNotMatch() {
            fail("implement me");
        }
        
        @Test
        public void updateEmail_should_ThrowAlreadyExists_when_NewEmailTakenByAnotherUser() {
            fail("implement me");
        }
        
        @Test
        public void updateEmail_should_ThrowNoAuthentication_when_SecurityContextIsEmpty() {
            fail("implement me");
        }
    }
    
    @Nested
    @DisplayName("deleteByEmail")
    class deleteByEmail {
        
        @Test
        public void deleteByEmail_should_DeleteUser_when_EmailExists() {
            fail("implement me");
        }
    }
}