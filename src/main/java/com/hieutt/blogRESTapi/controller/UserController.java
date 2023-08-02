package com.hieutt.blogRESTapi.controller;

import com.hieutt.blogRESTapi.dto.UserDto;
import com.hieutt.blogRESTapi.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("")
    public ResponseEntity<List<UserDto>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getUserById(@PathVariable(value = "id") Long id) {
        return ResponseEntity.ok(userService.getUser(id));
    }

    @PreAuthorize("hasAnyAuthority('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable(value = "id") Long id) {
        userService.deleteUser(id);
        return ResponseEntity.ok("This user has been deleted!");
    }

    @PutMapping("/{userId}/follow")
    public ResponseEntity<String> followUser(@PathVariable(value = "userId") Long userId,
                                             Authentication authentication) {
        String message = userService.followUser(userId, authentication);
        return ResponseEntity.ok("You just " + message + " this user");
    }

    @GetMapping("/{userId}/followers")
    public ResponseEntity<List<UserDto>> getFollowers(@PathVariable(value = "userId") Long userId) {
        return ResponseEntity.ok(userService.getFollowers(userId));
    }

    @GetMapping("/{userId}/followings")
    public ResponseEntity<List<UserDto>> getFollowings(@PathVariable(value = "userId") Long userId) {
        return ResponseEntity.ok(userService.getFollowings(userId));
    }
}
