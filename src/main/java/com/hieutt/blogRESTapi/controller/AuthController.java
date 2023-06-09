package com.hieutt.blogRESTapi.controller;

import com.hieutt.blogRESTapi.dto.JwtAuthResponse;
import com.hieutt.blogRESTapi.dto.RegisterDto;
import com.hieutt.blogRESTapi.dto.SignInDto;
import com.hieutt.blogRESTapi.security.AuthenticationService;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
    private final AuthenticationService authenticationService;

    public AuthController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @PostMapping("/register")
    public ResponseEntity<JwtAuthResponse> register(@RequestBody @Valid RegisterDto registerDto) {
        return ResponseEntity.ok(authenticationService.register(registerDto));
    }

    @PostMapping("/signin")
    public ResponseEntity<JwtAuthResponse> signIn(@RequestBody @Valid SignInDto signInDto) {
        return ResponseEntity.ok(authenticationService.signIn(signInDto));
    }

    @PostMapping("/refresh-token")
    public void refreshToken(HttpServletRequest request,
                             HttpServletResponse response) throws IOException {
        authenticationService.refreshToken(request, response);
    }
}
