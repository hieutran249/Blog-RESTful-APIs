package com.hieutt.blogRESTapi.service;

import com.hieutt.blogRESTapi.dto.UserDto;

import java.util.List;

public interface UserService {
    List<UserDto> getAllUsers();

    UserDto getUser(Long id);

    void deleteUser(Long id);
}
