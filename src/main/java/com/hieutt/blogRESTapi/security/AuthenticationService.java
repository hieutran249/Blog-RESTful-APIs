package com.hieutt.blogRESTapi.security;

import com.hieutt.blogRESTapi.dto.JwtAuthResponse;
import com.hieutt.blogRESTapi.dto.RegisterDto;
import com.hieutt.blogRESTapi.dto.SignInDto;
import com.hieutt.blogRESTapi.entity.Role;
import com.hieutt.blogRESTapi.entity.User;
import com.hieutt.blogRESTapi.repository.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthenticationService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService, AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
    }

    public JwtAuthResponse register(RegisterDto registerDto) {
        // create User object
        User user = User.builder()
                .firstName(registerDto.getFirstName())
                .lastName(registerDto.getLastName())
                .email(registerDto.getEmail())
                .password(passwordEncoder.encode(registerDto.getPassword()))
                .role(Role.USER)
                .build();

        // save User into db
        userRepository.save(user);

        // generate jwt for User
        String jwt = jwtService.generateToken(user);

        return JwtAuthResponse.builder().token(jwt).build();
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

        return JwtAuthResponse.builder().token(jwt).build();
    }
}
