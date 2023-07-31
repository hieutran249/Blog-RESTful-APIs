package com.hieutt.blogRESTapi.service.impl;

import com.hieutt.blogRESTapi.dto.UserDto;
import com.hieutt.blogRESTapi.entity.User;
import com.hieutt.blogRESTapi.exception.ResourceNotFoundException;
import com.hieutt.blogRESTapi.repository.UserRepository;
import com.hieutt.blogRESTapi.service.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.Authentication;
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
    public List<UserDto> getAllUsers() {
        List<User> users = userRepository.findAll();

        return users.stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserDto getUser(Long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User", "id", id));

        return mapToDto(user);
    }

    @Override
    public void deleteUser(Long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User", "id", id));

        userRepository.delete(user);
    }

    @Override
    public String followUser(Long userId, Authentication authentication) {
        String message;
        // get current user
        User user = getCurrentUser(authentication);

        // get user that is going to be followed
        User followedUser = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        // check if user had followed the user
        if (userRepository.followedUser(followedUser.getId(), user.getId()) == 1) {
            user.getFollowings().remove(followedUser);
            followedUser.getFollowers().remove(user);
            userRepository.unfollowUser(followedUser.getId(), user.getId());
            message = "unfollowed";
        }
        else {
            user.getFollowings().add(followedUser);
            followedUser.getFollowers().add(user);
            userRepository.saveFollower(followedUser.getId(), user.getId());
            message = "followed";
        }
        userRepository.save(user);
        userRepository.save(followedUser);
        return message;

    }

    @Override
    public List<UserDto> getFollowers(Long userId) {
        userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        List<User> followers = userRepository.findFollowers(userId);
        return followers.stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<UserDto> getFollowings(Long userId) {
        userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        List<User> followers = userRepository.findFollowings(userId);
        return followers.stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    // convert Entity into DTO
    private UserDto mapToDto(User user) {
        // Mapping using ModelMapper
        return mapper.map(user, UserDto.class);
    }

    // convert DTO into Entity
    private User mapToEntity(UserDto userResponse) {
        // Mapping using ModelMapper
        return mapper.map(userResponse, User.class);
    }

    private User getCurrentUser(Authentication authentication) {
        // get current user
        String email = authentication.getName();
        return userRepository.findByEmail(email).orElseThrow(
                () -> new ResourceNotFoundException("User", "email", email));
    }
}
