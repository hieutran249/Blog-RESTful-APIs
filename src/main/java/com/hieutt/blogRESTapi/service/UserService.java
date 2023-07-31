package com.hieutt.blogRESTapi.service;

import com.hieutt.blogRESTapi.dto.UserDto;
import org.springframework.security.core.Authentication;

import java.util.List;

public interface UserService {
    List<UserDto> getAllUsers();

    UserDto getUser(Long id);

    void deleteUser(Long id);

    String followUser(Long userId, Authentication authentication);

    List<UserDto> getFollowers(Long userId);

    List<UserDto> getFollowings(Long userId);
}
