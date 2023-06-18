package com.hieutt.blogRESTapi.service;

import com.hieutt.blogRESTapi.dto.UserDto;
import org.springframework.security.core.Authentication;

public interface CurrentUserService {
    UserDto getCurrentUser(Authentication authentication);

    UserDto updateCurrentUser(Authentication authentication, UserDto userDto);

    void deleteCurrentUser(Authentication authentication);
}
