package com.hieutt.blogRESTapi.security;

import com.hieutt.blogRESTapi.repository.TokenRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
//    // inject dependencies
//    private final JwtTokenProvider jwtTokenProvider;
//    private final CustomUserDetailsService customUserDetailsService;
//
//    public JwtAuthenticationFilter(JwtTokenProvider jwtTokenProvider, CustomUserDetailsService customUserDetailsService) {
//        this.jwtTokenProvider = jwtTokenProvider;
//        this.customUserDetailsService = customUserDetailsService;
//    }

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;
    private final TokenRepository tokenRepository;

    public JwtAuthenticationFilter(JwtService jwtService, UserDetailsService userDetailsService, TokenRepository tokenRepository) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
        this.tokenRepository = tokenRepository;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {
//        // get JWT token from http request
//        String token = getJwtFromRequest(request);
//
//        // validate token
//        if (StringUtils.hasText(token) && jwtTokenProvider.validateToken(token)) {
//            // get username from token
//            String username = jwtTokenProvider.getUsernameFromJwt(token);
//
//            // load user associated with token
//            UserDetails userDetails = customUserDetailsService.loadUserByUsername(username);
//            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
//                    userDetails,null, userDetails.getAuthorities()
//            );
//            authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
//
//            // set spring
//            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
//        }
//
//        filterChain.doFilter(request, response);

        final String authHeader = request.getHeader("Authorization");
        final String userEmail;
        final String jwt;

        if (!StringUtils.hasText(authHeader)
                || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }
        jwt = authHeader.substring(7);

        // get userEmail from jwtToken
        userEmail = jwtService.extractUsername(jwt);

        // check if email is null and if user is authenticated yet
        if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            // get UserDetails from the userEmail
            UserDetails userDetails = userDetailsService.loadUserByUsername(userEmail);
            // check if token is revoked and expired and map it into boolean (if save token in DB)
            boolean isTokenValid = tokenRepository.findByToken(jwt)
                    .map(t -> !t.isRevoked() && !t.isExpired())
                    .orElse(false);
            // validate token
            if (jwtService.isTokenValid(jwt) && isTokenValid) {
                // Update Security Context
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                    userDetails,null,userDetails.getAuthorities());
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }
        filterChain.doFilter(request, response);
    }
//
//    // Bearer <accessToken>
//    private String getJwtFromRequest(HttpServletRequest request) {
//        String bearerToken = request.getHeader("Authorization");
//        if (StringUtils.hasText(bearerToken)
//                && bearerToken.startsWith("Bearer ")) {
//            return bearerToken.substring(7);
//        }
//        return null;
//    }
}
