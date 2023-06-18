package com.hieutt.blogRESTapi.controller;

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
        return ResponseEntity.ok(currentUserService.getCurrentUser(authentication));
    }

    @PutMapping
    public ResponseEntity<UserDto> updateCurrentUser(Authentication authentication,
                                                     @RequestBody UserDto userDto) {
        return ResponseEntity.ok(currentUserService.updateCurrentUser(authentication, userDto));
    }

    @DeleteMapping
    public ResponseEntity<String> deleteCurrentUser(Authentication authentication) {
        currentUserService.deleteCurrentUser(authentication);
        return ResponseEntity.ok("This user has been deleted!");
    }
}
