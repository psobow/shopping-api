package com.sobow.shopping.controllers.user;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sobow.shopping.controllers.user.requests.PasswordRequest;
import com.sobow.shopping.controllers.user.requests.UserProfileUpdateRequest;
import com.sobow.shopping.controllers.user.requests.self.SelfEmailUpdateRequest;
import com.sobow.shopping.controllers.user.requests.self.SelfPasswordUpdateRequest;
import com.sobow.shopping.controllers.user.requests.self.SelfUserCreateRequest;
import com.sobow.shopping.controllers.user.requests.self.SelfUserDeleteRequest;
import com.sobow.shopping.controllers.user.requests.self.SelfUserPartialUpdateRequest;
import com.sobow.shopping.controllers.user.responses.UserResponse;
import com.sobow.shopping.domain.user.User;
import com.sobow.shopping.exceptions.EmailAlreadyExistsException;
import com.sobow.shopping.security.Impl.UserDetailsImpl;
import com.sobow.shopping.services.user.CurrentUserService;
import com.sobow.shopping.services.user.UserService;
import com.sobow.shopping.utils.TestFixtures;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false)
public class UserControllerTests {
    
    @MockitoBean
    private CurrentUserService currentUserService;
    
    @MockitoBean
    private UserService userService;
    
    @Autowired
    MockMvc mockMvc;
    
    private final static String REGISTER_PATH = "/api/users/register";
    private final static String ME_PATH = "/api/users/me";
    private final static String ME_PASSWORD_PATH = ME_PATH + "/password";
    private final static String ME_EMAIL_PATH = ME_PATH + "/email";
    
    private final TestFixtures fixtures = new TestFixtures();
    
    private static final ObjectMapper objectMapper = new ObjectMapper();
    
    @Nested
    @DisplayName("selfCreate")
    class selfCreate {
        
        @Test
        public void selfCreate_should_Return201_when_ValidRequest() throws Exception {
            // Given
            SelfUserCreateRequest request = fixtures.selfCreateUserRequest();
            User user = fixtures.userEntity();
            ReflectionTestUtils.setField(user, "id", fixtures.userId());
            UserResponse response = fixtures.userResponse();
            
            when(userService.selfCreate(request)).thenReturn(user);
            when(currentUserService.mapToUserResponse(fixtures.userId())).thenReturn(response);
            
            String json = objectMapper.writeValueAsString(request);
            
            // When & Then
            mockMvc.perform(post(REGISTER_PATH)
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                                .content(json))
                   .andExpect(status().isCreated())
                   .andExpect(jsonPath("$.message").value("User created"))
                   .andExpect(jsonPath("$.data").exists());
        }
        
        @Test
        public void selfCreate_should_Return400_when_InvalidRequestBody() throws Exception {
            // Given
            SelfUserCreateRequest request = new SelfUserCreateRequest("  ", new PasswordRequest("a"), null);
            String json = objectMapper.writeValueAsString(request);
            
            // When & Then
            mockMvc.perform(post(REGISTER_PATH)
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                                .content(json))
                   .andExpect(status().isBadRequest());
        }
        
        @Test
        public void selfCreate_should_Return409_when_EmailAlreadyExists() throws Exception {
            SelfUserCreateRequest request = fixtures.selfCreateUserRequest();
            when(userService.selfCreate(request)).thenThrow(new EmailAlreadyExistsException(request.email()));
            String json = objectMapper.writeValueAsString(request);
            
            // When & Then
            mockMvc.perform(post(REGISTER_PATH)
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                                .content(json))
                   .andExpect(status().isConflict());
        }
    }
    
    @Nested
    @DisplayName("selfGet")
    class selfGet {
        
        @Test
        void selfGet_should_Return200_when_Authenticated() throws Exception {
            // Given
            User user = fixtures.userEntity();
            ReflectionTestUtils.setField(user, "id", fixtures.userId());
            
            UserDetailsImpl userDetails = new UserDetailsImpl(user);
            var auth = new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities());
            
            SecurityContext context = SecurityContextHolder.createEmptyContext();
            context.setAuthentication(auth);
            SecurityContextHolder.setContext(context);
            
            UserResponse response = fixtures.userResponse();
            
            when(currentUserService.mapToUserResponse(fixtures.userId())).thenReturn(response);
            
            // When & Then
            mockMvc.perform(get(ME_PATH))
                   .andExpect(status().isOk())
                   .andExpect(jsonPath("$.message").value("Logged in user"))
                   .andExpect(jsonPath("$.data").exists());
        }
    }
    
    @Nested
    @DisplayName("selfPartialUpdate")
    class selfPartialUpdate {
        
        @Test
        void selfPartialUpdate_should_Return200_when_ValidRequest() throws Exception {
            // Given
            SelfUserPartialUpdateRequest request = fixtures.selfUpdateUserRequest();
            User user = fixtures.userEntity();
            ReflectionTestUtils.setField(user, "id", fixtures.userId());
            UserResponse response = fixtures.userResponse();
            
            when(userService.selfPartialUpdate(request)).thenReturn(user);
            when(currentUserService.mapToUserResponse(fixtures.userId())).thenReturn(response);
            
            String json = objectMapper.writeValueAsString(request);
            // When & Then
            mockMvc.perform(put(ME_PATH)
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                                .content(json))
                   .andExpect(status().isOk());
        }
        
        @Test
        void selfPartialUpdate_should_Return400_when_InvalidRequestBody() throws Exception {
            SelfUserPartialUpdateRequest request = new SelfUserPartialUpdateRequest(new UserProfileUpdateRequest("  ", "", null));
            String json = objectMapper.writeValueAsString(request);
            // When & Then
            mockMvc.perform(put(ME_PATH)
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                                .content(json))
                   .andExpect(status().isBadRequest());
        }
    }
    
    @Nested
    @DisplayName("selfChangedPassword")
    class selfChangedPassword {
        
        @Test
        void selfChangedPassword_should_Return204_when_ValidRequest() throws Exception {
            // Given
            SelfPasswordUpdateRequest request = fixtures.passwordUpdateRequest();
            String json = objectMapper.writeValueAsString(request);
            
            // When & Then
            mockMvc.perform(post(ME_PASSWORD_PATH)
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                                .content(json))
                   .andExpect(status().isNoContent());
        }
        
        @Test
        void selfChangedPassword_should_Return400_when_InvalidRequestBody() throws Exception {
            // Given
            SelfPasswordUpdateRequest request = new SelfPasswordUpdateRequest(new PasswordRequest("   "), new PasswordRequest(null));
            String json = objectMapper.writeValueAsString(request);
            
            // When & Then
            mockMvc.perform(post(ME_PASSWORD_PATH)
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                                .content(json))
                   .andExpect(status().isBadRequest());
        }
    }
    
    @Nested
    @DisplayName("selfChangedEmail")
    class selfChangedEmail {
        
        @Test
        void selfChangedEmail_should_Return204_when_ValidRequest() throws Exception {
            // Given
            SelfEmailUpdateRequest request = fixtures.emailUpdateRequest();
            String json = objectMapper.writeValueAsString(request);
            
            // When & Then
            mockMvc.perform(post(ME_EMAIL_PATH)
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                                .content(json))
                   .andExpect(status().isNoContent());
        }
        
        @Test
        void selfChangedEmail_should_Return400_when_InvalidRequestBody() throws Exception {
            // Given
            SelfEmailUpdateRequest request = new SelfEmailUpdateRequest(new PasswordRequest(""), null);
            String json = objectMapper.writeValueAsString(request);
            
            // When & Then
            mockMvc.perform(post(ME_EMAIL_PATH)
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                                .content(json))
                   .andExpect(status().isBadRequest());
        }
    }
    
    @Nested
    @DisplayName("selfDelete")
    class selfDelete {
        
        @Test
        void selfDelete_should_Return204_when_Deleted() throws Exception {
            // Given
            SelfUserDeleteRequest request = fixtures.deleteRequest();
            String json = objectMapper.writeValueAsString(request);
            // When & Then
            mockMvc.perform(delete(ME_PATH)
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                                .content(json))
                   .andExpect(status().isNoContent());
        }
    }
}
