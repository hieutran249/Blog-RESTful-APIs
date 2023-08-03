package com.hieutt.blogRESTapi.controller;

import com.hieutt.blogRESTapi.dto.UserDto;
import com.hieutt.blogRESTapi.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "User")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @Operation(
            description = "This endpoint gets all users",
            summary = "Get All Users",
            responses = {
                    @ApiResponse(
                            description = "Success",
                            responseCode = "200"
                    )
            }
    )
    @GetMapping
    public ResponseEntity<List<UserDto>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @Operation(
            description = "This endpoint gets user based on provided user id",
            summary = "Get All Users By Id",
            responses = {
                    @ApiResponse(
                            description = "Success",
                            responseCode = "200"
                    )
            }
    )
    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getUserById(@PathVariable(value = "id") Long id) {
        return ResponseEntity.ok(userService.getUser(id));
    }

    @Operation(
            description = "This endpoint deletes user based on provided user id and return message",
            summary = "Delete User",
            responses = {
                    @ApiResponse(
                            description = "Success",
                            responseCode = "200"
                    ),
                    @ApiResponse(
                            description = "Unauthorized / Invalid Token",
                            responseCode = "403"
                    )
            }
    )
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable(value = "id") Long id) {
        userService.deleteUser(id);
        return ResponseEntity.ok("This user has been deleted!");
    }

    @Operation(
            description = "This endpoint lets logged in user follow user based on provided user id and return message",
            summary = "Follow User",
            responses = {
                    @ApiResponse(
                            description = "Success",
                            responseCode = "200"
                    ),
                    @ApiResponse(
                            description = "Unauthorized / Invalid Token",
                            responseCode = "403"
                    )
            }
    )
    @PutMapping("/{userId}/follow")
    public ResponseEntity<String> followUser(@PathVariable(value = "userId") Long userId,
                                             Authentication authentication) {
        String message = userService.followUser(userId, authentication);
        return ResponseEntity.ok("You just " + message + " this user");
    }

    @Operation(
            description = "This endpoint return all followers of logged in user based on provided user id and return message",
            summary = "Get Followers of User",
            responses = {
                    @ApiResponse(
                            description = "Success",
                            responseCode = "200"
                    ),
                    @ApiResponse(
                            description = "Unauthorized / Invalid Token",
                            responseCode = "403"
                    )
            }
    )
    @GetMapping("/{userId}/followers")
    public ResponseEntity<List<UserDto>> getFollowers(@PathVariable(value = "userId") Long userId) {
        return ResponseEntity.ok(userService.getFollowers(userId));
    }

    @Operation(
            description = "This endpoint return all followings of logged in user based on provided user id and return message",
            summary = "Get Followings of User",
            responses = {
                    @ApiResponse(
                            description = "Success",
                            responseCode = "200"
                    ),
                    @ApiResponse(
                            description = "Unauthorized / Invalid Token",
                            responseCode = "403"
                    )
            }
    )
    @GetMapping("/{userId}/followings")
    public ResponseEntity<List<UserDto>> getFollowings(@PathVariable(value = "userId") Long userId) {
        return ResponseEntity.ok(userService.getFollowings(userId));
    }
}
