package com.hieutt.blogRESTapi.service.impl;

import com.hieutt.blogRESTapi.dto.UserDto;
import com.hieutt.blogRESTapi.entity.User;
import com.hieutt.blogRESTapi.exception.BlogAPIException;
import com.hieutt.blogRESTapi.exception.ResourceNotFoundException;
import com.hieutt.blogRESTapi.repository.UserRepository;
import com.hieutt.blogRESTapi.service.CurrentUserService;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
public class CurrentUserServiceImpl implements CurrentUserService {
    private final UserRepository userRepository;
    private final ModelMapper mapper;

    public CurrentUserServiceImpl(UserRepository userRepository, ModelMapper mapper) {
        this.userRepository = userRepository;
        this.mapper = mapper;
    }

    @Override
    public UserDto getCurrentUser(Authentication authentication) {
        // get the principle of the current logged-in user
        UserDetails currentUserPrinciple = (UserDetails) authentication.getPrincipal();
        // get the email of the current user
        String email = currentUserPrinciple.getUsername();
        // get current user from db
        User currentUser = userRepository.findByEmail(email).orElseThrow(() -> new ResourceNotFoundException("User", "email", email));

        return mapToDto(currentUser);
    }

    @Override
    public UserDto updateCurrentUser(Authentication authentication, UserDto userDto) {
        User currentUser = mapToEntity(getCurrentUser(authentication));
        currentUser.setFirstName(userDto.getFirstName());
        currentUser.setLastName(userDto.getLastName());
        if (userRepository.existsByEmail(userDto.getEmail()) && !userDto.getEmail().equals(currentUser.getEmail()))
            throw new BlogAPIException(HttpStatus.BAD_REQUEST, "This email already exists!");
        currentUser.setEmail(userDto.getEmail());

        userRepository.save(currentUser);

        return mapToDto(currentUser);
    }

    @Override
    public void deleteCurrentUser(Authentication authentication) {
        User currentUser = mapToEntity(getCurrentUser(authentication));
        userRepository.delete(currentUser);
    }

    // convert Entity into DTO
    private UserDto mapToDto(User user) {
        // Mapping using ModelMapper
        return mapper.map(user, UserDto.class);
    }

    // convert DTO into Entity
    private User mapToEntity(UserDto userDto) {
        // Mapping using ModelMapper
        return mapper.map(userDto, User.class);
    }
}
