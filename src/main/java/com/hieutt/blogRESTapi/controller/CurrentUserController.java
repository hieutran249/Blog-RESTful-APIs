package com.hieutt.blogRESTapi.controller;

import com.hieutt.blogRESTapi.dto.PasswordDto;
import com.hieutt.blogRESTapi.dto.UserDto;
import com.hieutt.blogRESTapi.service.CurrentUserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/me")
public class CurrentUserController {
    private final CurrentUserService currentUserService;

    public CurrentUserController(CurrentUserService currentUserService) {
        this.currentUserService = currentUserService;
    }

    @GetMapping
    public ResponseEntity<UserDto> getCurrentUser(Authentication authentication) {
        return ResponseEntity.ok(currentUserService.getCurrentUserDto(authentication));
    }

    @PutMapping
    public ResponseEntity<UserDto> updateCurrentUser(Authentication authentication,
                                                     @RequestBody UserDto userDto) {
        return ResponseEntity.ok(currentUserService.updateCurrentUser(authentication, userDto));
    }

    @PutMapping("/change-password")
    public ResponseEntity<String> changePassword(Authentication authentication,
                                                 @RequestBody PasswordDto passwordDto) {
        currentUserService.changePassword(authentication, passwordDto);
        return ResponseEntity.ok("The password has been changed");
    }

    @DeleteMapping
    public ResponseEntity<String> deleteCurrentUser(Authentication authentication) {
        currentUserService.deleteCurrentUser(authentication);
        return ResponseEntity.ok("This user has been deleted!");
    }
}
