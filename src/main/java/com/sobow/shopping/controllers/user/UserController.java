package com.sobow.shopping.controllers.user;

import com.sobow.shopping.controllers.ApiResponseDto;
import com.sobow.shopping.controllers.user.requests.self.SelfEmailUpdateRequest;
import com.sobow.shopping.controllers.user.requests.self.SelfPasswordUpdateRequest;
import com.sobow.shopping.controllers.user.requests.self.SelfUserCreateRequest;
import com.sobow.shopping.controllers.user.requests.self.SelfUserDeleteRequest;
import com.sobow.shopping.controllers.user.requests.self.SelfUserPartialUpdateRequest;
import com.sobow.shopping.controllers.user.responses.UserResponse;
import com.sobow.shopping.domain.user.User;
import com.sobow.shopping.security.Impl.UserDetailsImpl;
import com.sobow.shopping.services.user.CurrentUserService;
import com.sobow.shopping.services.user.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping(path = "${api.prefix}/users")
@Tag(name = "User Controller", description = "API for managing user account by User")
public class UserController {
    
    private final UserService userService;
    private final CurrentUserService currentUserService;
    
    @Operation(
        summary = "Create a new user",
        requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
            required = true,
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(value = """
                    {
                        "email": "newuser@gmail.com",
                        "password": "password",
                        "userProfile": {
                            "firstName": "John",
                            "lastName": "Doe",
                            "userAddress": {
                                "cityName": "Warsaw",
                                "streetName": "street",
                                "streetNumber": "30",
                                "postCode": "10-200"
                            }
                        }
                    }
                    """
                )
            )
        )
    )
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "User created"),
        @ApiResponse(responseCode = "400", description = "Validation error"),
        @ApiResponse(responseCode = "409", description = "Email already exists")
    })
    @PostMapping("/register")
    public ResponseEntity<ApiResponseDto> selfCreate(
        @RequestBody @Valid SelfUserCreateRequest createRequest
    ) {
        User user = userService.selfCreate(createRequest);
        UserResponse response = currentUserService.mapToUserResponse(user.getId());
        return ResponseEntity.status(HttpStatus.CREATED)
                             .body(new ApiResponseDto("User created", response));
    }
    
    @Operation(
        summary = "Get info about authenticated user",
        security = {@SecurityRequirement(name = "bearerAuth")}
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Logged in user"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    @GetMapping(path = "/me")
    public ResponseEntity<ApiResponseDto> selfGet(
        @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        User user = userDetails.getUser();
        UserResponse response = currentUserService.mapToUserResponse(user.getId());
        return ResponseEntity.ok(new ApiResponseDto("Logged in user", response));
    }
    
    @Operation(
        summary = "Partially update authenticated user",
        security = {@SecurityRequirement(name = "bearerAuth")},
        requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
            required = true,
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(value = """
                    {
                        "userProfile": {
                            "firstName": "first name updated",
                            "lastName": "last name updated",
                            "userAddress": {
                                "cityName": "city name updated",
                                "streetName": "street name updated",
                                "streetNumber": " streetNumber updated",
                                "postCode": "10-300"
                            }
                        }
                    }
                    """
                )
            )
        )
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "User updated"),
        @ApiResponse(responseCode = "400", description = "Validation error"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    @PutMapping(path = "/me")
    public ResponseEntity<ApiResponseDto> selfPartialUpdate(
        @RequestBody @Valid SelfUserPartialUpdateRequest updateRequest
    ) {
        User user = userService.selfPartialUpdate(updateRequest);
        UserResponse response = currentUserService.mapToUserResponse(user.getId());
        return ResponseEntity.ok(new ApiResponseDto("User updated", response));
    }
    
    @Operation(
        summary = "Update authenticated user's password",
        security = {@SecurityRequirement(name = "bearerAuth")},
        requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
            required = true,
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(value = """
                    {
                        "password": "password",
                        "newPassword": "password123"
                    }
                    """
                )
            )
        )
    )
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Password updated"),
        @ApiResponse(responseCode = "400", description = "Validation error"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    @PostMapping(path = "/me/password")
    public ResponseEntity<Void> selfUpdatePassword(
        @RequestBody @Valid SelfPasswordUpdateRequest updateRequest
    ) {
        userService.selfUpdatePassword(updateRequest);
        return ResponseEntity.noContent().build();
    }
    
    @Operation(
        summary = "Update authenticated user's email",
        security = {@SecurityRequirement(name = "bearerAuth")},
        requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
            required = true,
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(value = """
                    {
                        "password": "password",
                        "newEmail": "admin@email.com"
                    }
                    """
                )
            )
        )
    )
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Email updated"),
        @ApiResponse(responseCode = "400", description = "Validation error"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    @PostMapping(path = "/me/email")
    public ResponseEntity<Void> selfUpdateEmail(
        @RequestBody @Valid SelfEmailUpdateRequest updateRequest
    ) {
        userService.selfUpdateEmail(updateRequest);
        return ResponseEntity.noContent().build();
    }
    
    @Operation(
        summary = "Delete authenticated user account",
        security = {@SecurityRequirement(name = "bearerAuth")},
        requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
            required = true,
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(value = """
                    {
                        "password": "password"
                    }
                    """
                )
            )
        )
    )
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "User deleted"),
        @ApiResponse(responseCode = "400", description = "Validation error"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    @DeleteMapping(path = "/me")
    public ResponseEntity<Void> selfDelete(
        @RequestBody @Valid SelfUserDeleteRequest deleteRequest
    ) {
        userService.selfDelete(deleteRequest);
        return ResponseEntity.noContent().build();
    }
}
