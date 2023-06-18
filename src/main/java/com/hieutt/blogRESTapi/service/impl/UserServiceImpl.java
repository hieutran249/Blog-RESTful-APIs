package com.hieutt.blogRESTapi.service.impl;

import com.hieutt.blogRESTapi.dto.UserDto;
import com.hieutt.blogRESTapi.dto.UserResponse;
import com.hieutt.blogRESTapi.entity.User;
import com.hieutt.blogRESTapi.exception.ResourceNotFoundException;
import com.hieutt.blogRESTapi.repository.UserRepository;
import com.hieutt.blogRESTapi.service.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final ModelMapper mapper;

    public UserServiceImpl(UserRepository userRepository, ModelMapper mapper) {
        this.userRepository = userRepository;
        this.mapper = mapper;
    }

    @Override
    public List<UserResponse> getAllUsers() {
        List<User> users = userRepository.findAll();

        return users.stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserResponse getUser(Long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User", "id", id));

        return mapToDto(user);
    }

    @Override
    public void deleteUser(Long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User", "id", id));

        userRepository.delete(user);
    }

    // convert Entity into DTO
    private UserResponse mapToDto(User user) {
        // Mapping using ModelMapper
        return mapper.map(user, UserResponse.class);
    }

    // convert DTO into Entity
    private User mapToEntity(UserResponse userResponse) {
        // Mapping using ModelMapper
        return mapper.map(userResponse, User.class);
    }
}
