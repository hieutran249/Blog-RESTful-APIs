package com.hieutt.blogRESTapi.service;

import com.hieutt.blogRESTapi.dto.PasswordDto;
import com.hieutt.blogRESTapi.dto.UserDto;
import org.springframework.security.core.Authentication;

public interface CurrentUserService {
    UserDto getCurrentUserDto(Authentication authentication);

    UserDto updateCurrentUser(Authentication authentication, UserDto userDto);

    void deleteCurrentUser(Authentication authentication);

    void changePassword(Authentication authentication, PasswordDto passwordDto);
}
