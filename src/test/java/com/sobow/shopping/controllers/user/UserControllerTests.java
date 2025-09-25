package com.sobow.shopping.controllers.user;

import static org.junit.jupiter.api.Assertions.fail;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sobow.shopping.mappers.user.responses.UserResponseMapper;
import com.sobow.shopping.services.UserService;
import com.sobow.shopping.utils.TestFixtures;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false)
public class UserControllerTests {
    
    @MockitoBean
    private UserResponseMapper userResponseMapper;
    
    @MockitoBean
    private UserService userService;
    
    @Autowired
    MockMvc mockMvc;
    
    private final TestFixtures fixtures = new TestFixtures();
    
    private static final ObjectMapper objectMapper = new ObjectMapper();
    
    @Nested
    @DisplayName("selfCreate")
    class selfCreate {
        
        @Test
        public void selfCreate_should_Return201_when_ValidRequest() {
            fail("implement me");
        }
        
        @Test
        public void selfCreate_should_Return400_when_InvalidRequestBody() {
            fail("implement me");
        }
        
        @Test
        public void selfCreate_should_Return409_when_EmailAlreadyExists() {
            fail("implement me");
        }
    }
    
    @Nested
    @DisplayName("selfGet")
    class selfGet {
        
        @Test
        void selfGet_should_Return200_when_Authenticated() {
            fail("implement me");
        }
    }
    
    @Nested
    @DisplayName("selfPartialUpdate")
    class selfPartialUpdate {
        
        @Test
        void selfPartialUpdate_should_Return200_when_ValidRequest() {
            fail("implement me");
        }
        
        @Test
        void selfPartialUpdate_should_Return400_when_InvalidRequestBody() {
            fail("implement me");
        }
    }
    
    @Nested
    @DisplayName("selfChangedPassword")
    class selfChangedPassword {
        
        @Test
        void selfChangedPassword_should_Return204_when_ValidRequest() {
            fail("implement me");
        }
        
        @Test
        void selfChangedPassword_should_Return400_when_InvalidRequestBody() {
            fail("implement me");
        }
    }
    
    @Nested
    @DisplayName("selfChangedEmail")
    class selfChangedEmail {
        
        @Test
        void selfChangedEmail_should_Return204_when_ValidRequest() {
            fail("implement me");
        }
        
        @Test
        void selfChangedEmail_should_Return400_when_InvalidRequestBody() {
            fail("implement me");
        }
    }
    
    @Nested
    @DisplayName("selfDelete")
    class selfDelete {
        
        @Test
        void selfDelete_should_Return204_when_Deleted() {
            fail("implement me");
        }
    }
}
