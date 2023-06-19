package com.hieutt.blogRESTapi.security;

import com.hieutt.blogRESTapi.dto.JwtAuthResponse;
import com.hieutt.blogRESTapi.dto.RegisterDto;
import com.hieutt.blogRESTapi.dto.SignInDto;
import com.hieutt.blogRESTapi.entity.Role;
import com.hieutt.blogRESTapi.entity.Token;
import com.hieutt.blogRESTapi.entity.TokenType;
import com.hieutt.blogRESTapi.entity.User;
import com.hieutt.blogRESTapi.exception.BlogAPIException;
import com.hieutt.blogRESTapi.repository.TokenRepository;
import com.hieutt.blogRESTapi.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AuthenticationService {
    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthenticationService(UserRepository userRepository, TokenRepository tokenRepository, PasswordEncoder passwordEncoder, JwtService jwtService, AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.tokenRepository = tokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
    }

    public JwtAuthResponse register(RegisterDto registerDto) {
        if (registerDto.getPassword().equals(registerDto.getConfirmPassword())) {
            // create User object
            User user = User.builder()
                    .firstName(registerDto.getFirstName())
                    .lastName(registerDto.getLastName())
                    .email(registerDto.getEmail())
                    .password(passwordEncoder.encode(registerDto.getPassword()))
                    .role(Role.USER)
                    .build();

            // save User into db
            User savedUser = userRepository.save(user);

            // generate jwt for User
            String jwt = jwtService.generateToken(user);

            // revoke all other tokens before saving new one
            revokeAllUserTokens(user);

            // save Token into db
            saveUserToken(savedUser, jwt);

            return JwtAuthResponse.builder().token(jwt).build();

        } else throw  new BlogAPIException(HttpStatus.BAD_REQUEST, "This password is not the same!");
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

        // revoke all other tokens before saving new one
        revokeAllUserTokens(user);

        // save Token into db
        saveUserToken(user, jwt);

        return JwtAuthResponse.builder().token(jwt).build();
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
}
