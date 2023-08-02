package com.hieutt.blogRESTapi.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hieutt.blogRESTapi.dto.JwtAuthResponse;
import com.hieutt.blogRESTapi.dto.RegisterDto;
import com.hieutt.blogRESTapi.dto.ResetPasswordDto;
import com.hieutt.blogRESTapi.dto.SignInDto;
import com.hieutt.blogRESTapi.entity.Role;
import com.hieutt.blogRESTapi.entity.Token;
import com.hieutt.blogRESTapi.entity.TokenType;
import com.hieutt.blogRESTapi.entity.User;
import com.hieutt.blogRESTapi.exception.BlogAPIException;
import com.hieutt.blogRESTapi.exception.ResourceNotFoundException;
import com.hieutt.blogRESTapi.repository.TokenRepository;
import com.hieutt.blogRESTapi.repository.UserRepository;
import com.hieutt.blogRESTapi.service.EmailService;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AuthenticationService {
    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final EmailService emailService;

    public AuthenticationService(UserRepository userRepository, TokenRepository tokenRepository,
                                 PasswordEncoder passwordEncoder, JwtService jwtService,
                                 AuthenticationManager authenticationManager, EmailService emailService) {
        this.userRepository = userRepository;
        this.tokenRepository = tokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
        this.emailService = emailService;
    }

    public void register(RegisterDto registerDto) throws MessagingException {
        if (registerDto.getPassword().equals(registerDto.getConfirmPassword())) {
            // create User object
            User user = User.builder()
                    .email(registerDto.getEmail())
                    .username(registerDto.getUsername())
                    .displayedName(registerDto.getDisplayedName())
                    .password(passwordEncoder.encode(registerDto.getPassword()))
                    .role(Role.USER)
                    .createdAt(LocalDateTime.now())
                    .build();

            // save User into db
            User savedUser = userRepository.save(user);

            // variables in template
            Map<String, Object> variables = new HashMap<>();
            variables.put("name", user.getDisplayedName());

            // send welcome email to new user
            emailService.sendEmail(user.getEmail(), "[BLOG] Welcome to Blog", variables, "welcome");

        } else throw new BlogAPIException(HttpStatus.BAD_REQUEST, "This password is not the same!");
    }

    public JwtAuthResponse signIn(SignInDto signInDto) {
        // authenticate by email and password
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        signInDto.getEmail(),
                        signInDto.getPassword()
                )
        );

        // find user by email
        User user = userRepository.findByEmail(signInDto.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException(
                        "User not found with email: " + signInDto.getEmail()));

        // generate jwt for User
        String jwt = jwtService.generateToken(user);
        // generate refresh token
        String refreshToken = jwtService.generateRefreshToken(user);

        // revoke all other tokens before saving new one
        revokeAllUserTokens(user);

        // save Token into db
        saveUserToken(user, jwt);

        return JwtAuthResponse.builder()
                .accessToken(jwt)
                .refreshToken(refreshToken)
                .build();
    }

    private void saveUserToken(User user, String jwt) {
        Token token = Token.builder()
                .user(user)
                .token(jwt)
                .tokenType(TokenType.BEARER)
                .revoked(false)
                .expired(false)
                .build();
        tokenRepository.save(token);
    }

    private void revokeAllUserTokens(User user) {
        List<Token> validUserTokens = tokenRepository.findAllValidTokensByUser(user.getId());
        if (validUserTokens.isEmpty()) return;

        validUserTokens.forEach(t -> {
            t.setExpired(true);
            t.setRevoked(true);
        });

        tokenRepository.saveAll(validUserTokens);
    }

    public JwtAuthResponse refreshToken(HttpServletRequest request,
                             HttpServletResponse response) throws IOException {
        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        final String userEmail;
        final String refreshToken;

        if (!StringUtils.hasText(authHeader)
                || !authHeader.startsWith("Bearer ")) {
            throw new BlogAPIException(HttpStatus.BAD_REQUEST, "Please log in to get refresh token!");
        }
        refreshToken = authHeader.substring(7);

        // get userEmail from jwtToken
        userEmail = jwtService.extractUsername(refreshToken);

        JwtAuthResponse authResponse = null;

        // check if email is null and if user is authenticated yet
        if (userEmail != null) {
            // get UserDetails from the userEmail
            User user = userRepository.findByEmail(userEmail)
                    .orElseThrow();
            // validate token
            if (jwtService.isTokenValid(refreshToken)) {
                String accessToken = jwtService.generateToken(user);
                revokeAllUserTokens(user);
                saveUserToken(user, accessToken);
                authResponse = JwtAuthResponse.builder()
                        .accessToken(accessToken)
                        .refreshToken(refreshToken)
                        .build();
            }
        } else throw new BlogAPIException(HttpStatus.BAD_REQUEST, "Cannot extract email from the token!");

        return authResponse;
    }

    public void forgotPassword(String email) throws MessagingException {
        // get user from email
        User user = userRepository.findByEmail(email).orElseThrow(() -> new ResourceNotFoundException("User", "email", email));

        // generate reset token
        String resetToken = jwtService.generateResetPassToken(user);

        // variables in template
        Map<String, Object> variables = new HashMap<>();
        variables.put("name", user.getDisplayedName());
        variables.put("link", "localhost:8080/api/auth/reset-password/" + resetToken);

        emailService.sendEmail(email, "Forgot Password", variables, "forgot-password");
    }


    public void resetPassword(ResetPasswordDto resetPasswordDto, String resetToken) {
        // extract email from token
        String email = jwtService.extractUsername(resetToken);

        // check if email is null and if user is authenticated yet and validate token
        if (email != null && jwtService.isTokenValid(resetToken)) {
            // get UserDetails from the userEmail
            User user = userRepository.findByEmail(email).orElseThrow(() -> new ResourceNotFoundException("User", "email", email));
            // check if the confirm password is the same as the new one
            if (!resetPasswordDto.getNewPassword().equals(resetPasswordDto.getConfirmPassword())) {
                throw new BlogAPIException(HttpStatus.BAD_REQUEST, "This password is not the same!");
            }
            user.setPassword(passwordEncoder.encode(resetPasswordDto.getNewPassword()));
            userRepository.save(user);
        }
        else throw new BlogAPIException(HttpStatus.BAD_REQUEST, "This reset token is not valid!");
    }
}
