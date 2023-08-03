package com.hieutt.blogRESTapi.controller;

import com.hieutt.blogRESTapi.dto.JwtAuthResponse;
import com.hieutt.blogRESTapi.dto.RegisterDto;
import com.hieutt.blogRESTapi.dto.ResetPasswordDto;
import com.hieutt.blogRESTapi.dto.SignInDto;
import com.hieutt.blogRESTapi.security.AuthenticationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "Authentication")
public class AuthController {
    private final AuthenticationService authenticationService;

    public AuthController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @Operation(
            description = "This endpoint lets user register a new account and return message",
            summary = "Register",

            responses = {
                    @ApiResponse(
                            description = "Success",
                            responseCode = "200"
                    )
            },
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Email, displayed name, username, password, confirm password",
                    required = true
            )
    )
    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody @Valid RegisterDto registerDto) throws MessagingException {
        authenticationService.register(registerDto);
        return ResponseEntity.ok("Registered successfully");
    }

    @Operation(
            description = "This endpoint lets user log in and return tokens",
            summary = "Sign in",

            responses = {
                    @ApiResponse(
                            description = "Success",
                            responseCode = "200"
                    )
            },
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Email and password",
                    required = true
            )
    )
    @PostMapping("/sign-in")
    public ResponseEntity<JwtAuthResponse> signIn(@RequestBody @Valid SignInDto signInDto) {
        return ResponseEntity.ok(authenticationService.signIn(signInDto));
    }

    @Operation(
            description = "This endpoint lets logged in user to refresh their access token and return tokens",
            summary = "Refresh Token",

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
    @PostMapping("/refresh-token")
    public JwtAuthResponse refreshToken(HttpServletRequest request,
                             HttpServletResponse response) throws IOException {
        return authenticationService.refreshToken(request, response);
    }

    @Operation(
            description = "This endpoint lets user to request a password reset and return message",
            summary = "Reset Password Request",

            responses = {
                    @ApiResponse(
                            description = "Success",
                            responseCode = "200"
                    )
            },
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Email",
                    required = true
            )
    )
    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(@RequestBody String email) throws MessagingException {
        authenticationService.forgotPassword(email);
        return ResponseEntity.ok("Please check your email to reset your password");
    }

    @Operation(
            description = "This endpoint lets user to reset their password with a reset password token and return message",
            summary = "Reset Password",

            responses = {
                    @ApiResponse(
                            description = "Success",
                            responseCode = "200"
                    )
            },
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "New password and confirm password",
                    required = true
            )
    )
    @PostMapping("/reset-password/{resetToken}")
    public ResponseEntity<String> resetPassword(@RequestBody ResetPasswordDto resetPasswordDto,
                                                @PathVariable(value = "resetToken") String resetToken) {
        authenticationService.resetPassword(resetPasswordDto, resetToken);
        return ResponseEntity.ok("Successfully reset your password 🎉 Try to login again");
    }
}
