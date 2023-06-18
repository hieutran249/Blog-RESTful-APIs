package com.hieutt.blogRESTapi.service;

import com.hieutt.blogRESTapi.dto.UserDto;
import com.hieutt.blogRESTapi.dto.UserResponse;

import java.util.List;

public interface UserService {
    List<UserResponse> getAllUsers();

    UserResponse getUser(Long id);

    void deleteUser(Long id);
}
