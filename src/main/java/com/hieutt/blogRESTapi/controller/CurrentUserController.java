package com.hieutt.blogRESTapi.controller;

import com.hieutt.blogRESTapi.dto.PasswordDto;
import com.hieutt.blogRESTapi.dto.UserDto;
import com.hieutt.blogRESTapi.service.CurrentUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/me")
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Current User")
public class CurrentUserController {
    private final CurrentUserService currentUserService;

    public CurrentUserController(CurrentUserService currentUserService) {
        this.currentUserService = currentUserService;
    }

    @Operation(
            description = "This endpoint get logged in user",
            summary = "Get Current User",

            responses = {
                    @ApiResponse(
                            description = "Success",
                            responseCode = "200"
                    ),
                    @ApiResponse(
                            description = "Unauthorized / Invalid Token",
                            responseCode = "403"
                    )
            }
    )
    @GetMapping
    public ResponseEntity<UserDto> getCurrentUser(Authentication authentication) {
        return ResponseEntity.ok(currentUserService.getCurrentUserDto(authentication));
    }

    @Operation(
            description = "This endpoint lets logged in user update current user and returned updated user",
            summary = "Update Current User",

            responses = {
                    @ApiResponse(
                            description = "Success",
                            responseCode = "200"
                    ),
                    @ApiResponse(
                            description = "Unauthorized / Invalid Token",
                            responseCode = "403"
                    )
            },
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "User displayed name, username, email",
                    required = true
            )
    )
    @PutMapping
    public ResponseEntity<UserDto> updateCurrentUser(Authentication authentication,
                                                     @RequestBody UserDto userDto) {
        return ResponseEntity.ok(currentUserService.updateCurrentUser(authentication, userDto));
    }

    @Operation(
            description = "This endpoint lets logged in user change current user's password and return message",
            summary = "Change Password",

            responses = {
                    @ApiResponse(
                            description = "Success",
                            responseCode = "200"
                    ),
                    @ApiResponse(
                            description = "Unauthorized / Invalid Token",
                            responseCode = "403"
                    )
            },
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Old password, new password and confirm password",
                    required = true
            )
    )
    @PutMapping("/change-password")
    public ResponseEntity<String> changePassword(Authentication authentication,
                                                 @RequestBody PasswordDto passwordDto) {
        currentUserService.changePassword(authentication, passwordDto);
        return ResponseEntity.ok("The password has been changed");
    }

    @Operation(
            description = "This endpoint lets logged in user delete current user and return message",
            summary = "Delete Current User",

            responses = {
                    @ApiResponse(
                            description = "Success",
                            responseCode = "200"
                    ),
                    @ApiResponse(
                            description = "Unauthorized / Invalid Token",
                            responseCode = "403"
                    )
            },
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Comment content",
                    required = true
            )
    )
    @DeleteMapping
    public ResponseEntity<String> deleteCurrentUser(Authentication authentication) {
        currentUserService.deleteCurrentUser(authentication);
        return ResponseEntity.ok("This user has been deleted!");
    }
}
