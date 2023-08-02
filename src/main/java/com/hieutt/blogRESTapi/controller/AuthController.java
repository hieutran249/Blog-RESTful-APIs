package com.hieutt.blogRESTapi.controller;

import com.hieutt.blogRESTapi.dto.JwtAuthResponse;
import com.hieutt.blogRESTapi.dto.RegisterDto;
import com.hieutt.blogRESTapi.dto.ResetPasswordDto;
import com.hieutt.blogRESTapi.dto.SignInDto;
import com.hieutt.blogRESTapi.security.AuthenticationService;
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
public class AuthController {
    private final AuthenticationService authenticationService;

    public AuthController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody @Valid RegisterDto registerDto) throws MessagingException {
        authenticationService.register(registerDto);
        return ResponseEntity.ok("Registered successfully");
    }

    @PostMapping("/sign-in")
    public ResponseEntity<JwtAuthResponse> signIn(@RequestBody @Valid SignInDto signInDto) {
        return ResponseEntity.ok(authenticationService.signIn(signInDto));
    }

    @PostMapping("/refresh-token")
    public JwtAuthResponse refreshToken(HttpServletRequest request,
                             HttpServletResponse response) throws IOException {
        return authenticationService.refreshToken(request, response);
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(@RequestBody String email) throws MessagingException {
        authenticationService.forgotPassword(email);
        return ResponseEntity.ok("Please check your email to reset your password");
    }

    @PostMapping("/reset-password/{resetToken}")
    public ResponseEntity<String> resetPassword(@RequestBody ResetPasswordDto resetPasswordDto,
                                                @PathVariable(value = "resetToken") String resetToken) {
        authenticationService.resetPassword(resetPasswordDto, resetToken);
        return ResponseEntity.ok("Successfully reset your password 🎉 Try to login again");
    }
}
