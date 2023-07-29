package com.hieutt.blogRESTapi.security;

import com.hieutt.blogRESTapi.entity.Token;
import com.hieutt.blogRESTapi.repository.TokenRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class LogoutService implements LogoutHandler {
    private final TokenRepository tokenRepository;

    public LogoutService(TokenRepository tokenRepository) {
        this.tokenRepository = tokenRepository;
    }

    @Override
    public void logout(HttpServletRequest request,
                       HttpServletResponse response,
                       Authentication authentication) {
        // extract jwt from the request
        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        if (!StringUtils.hasText(authHeader)
                || !authHeader.startsWith("Bearer ")) {
            return;
        }
        jwt = authHeader.substring(7);
        // fetch Token from db
        Token storedToken = tokenRepository.findByToken(jwt).orElse(null);
        // invalidate token
        if (storedToken != null) {
            storedToken.setRevoked(true);
            storedToken.setExpired(true);
            tokenRepository.save(storedToken);
        }
    }
}
