package com.hieutt.blogRESTapi.controller;

import com.hieutt.blogRESTapi.dto.PostDto;
import com.hieutt.blogRESTapi.dto.PostResponse;
import com.hieutt.blogRESTapi.service.PostService;
import com.hieutt.blogRESTapi.utils.AppConstants;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/posts")
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Post")
public class PostController {
    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    @Operation(
            description = "This endpoint lets user create post and return newly created post",
            summary = "Create Post",

            responses = {
                    @ApiResponse(
                            description = "Success",
                            responseCode = "200"
                    ),
                    @ApiResponse(
                            description = "Unauthorized / Invalid Token",
                            responseCode = "403"
                    )
            },
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Post title, post content and post category",
                    required = true
            )
    )
    @PostMapping
    public ResponseEntity<PostDto> createPost(@Valid @RequestBody PostDto postDto,
                                              @RequestParam(value = "tags", required = false) String tags,
                                              Authentication authentication) {
        return new ResponseEntity<>(postService.createPost(postDto, tags, authentication), HttpStatus.CREATED);
    }

    @Operation(
            description = "This endpoint get all posts",
            summary = "Get All Posts",

            responses = {
                    @ApiResponse(
                            description = "Success",
                            responseCode = "200"
                    )
            }
    )
    @GetMapping
    public PostResponse getAllPosts(
            @RequestParam(value = "pageNo", defaultValue = AppConstants.DEFAULT_PAGE_NUMBER, required = false)
            int pageNo,
            @RequestParam(value = "pageSize", defaultValue = AppConstants.DEFAULT_PAGE_SIZE, required = false)
            int pageSize,
            @RequestParam(value = "sortBy", defaultValue = AppConstants.DEFAULT_SORT_BY, required = false)
            String sortBy,
            @RequestParam(value = "sortDir", defaultValue = AppConstants.DEFAULT_SORT_DIRECTION, required = false)
            String sortDir
    ) {
        return postService.getAllPosts(pageNo, pageSize, sortBy, sortDir);
    }

    @Operation(
            description = "This endpoint get all posts by category based on category id",
            summary = "Get Posts By Category",

            responses = {
                    @ApiResponse(
                            description = "Success",
                            responseCode = "200"
                    )
            }
    )
    @GetMapping("/category/{categoryId}")
    public PostResponse getPostsByCategory(
            @RequestParam(value = "pageNo", defaultValue = AppConstants.DEFAULT_PAGE_NUMBER, required = false)
            int pageNo,
            @RequestParam(value = "pageSize", defaultValue = AppConstants.DEFAULT_PAGE_SIZE, required = false)
            int pageSize,
            @RequestParam(value = "sortBy", defaultValue = AppConstants.DEFAULT_SORT_BY, required = false)
            String sortBy,
            @RequestParam(value = "sortDir", defaultValue = AppConstants.DEFAULT_SORT_DIRECTION, required = false)
            String sortDir,
            @PathVariable(value = "categoryId") Long categoryId) {
        return postService.getPostsByCategory(pageNo, pageSize, sortBy, sortDir, categoryId);
    }

    @Operation(
            description = "This endpoint get all posts by tag based on tag id",
            summary = "Get Posts By Tag",

            responses = {
                    @ApiResponse(
                            description = "Success",
                            responseCode = "200"
                    )
            }
    )
    @GetMapping("/tag/{tagId}")
    public PostResponse getPostsByTag(
            @RequestParam(value = "pageNo", defaultValue = AppConstants.DEFAULT_PAGE_NUMBER, required = false)
            int pageNo,
            @RequestParam(value = "pageSize", defaultValue = AppConstants.DEFAULT_PAGE_SIZE, required = false)
            int pageSize,
            @RequestParam(value = "sortBy", defaultValue = AppConstants.DEFAULT_SORT_BY, required = false)
            String sortBy,
            @RequestParam(value = "sortDir", defaultValue = AppConstants.DEFAULT_SORT_DIRECTION, required = false)
            String sortDir,
            @PathVariable(value = "tagId") Long tagId) {
        return postService.getPostsByTag(pageNo, pageSize, sortBy, sortDir, tagId);
    }

    @Operation(
            description = "This endpoint get all posts by user based on user id",
            summary = "Get Posts By User",

            responses = {
                    @ApiResponse(
                            description = "Success",
                            responseCode = "200"
                    )
            }
    )
    @GetMapping("/user/{userId}")
    public PostResponse getPostsByUser(
            @RequestParam(value = "pageNo", defaultValue = AppConstants.DEFAULT_PAGE_NUMBER, required = false)
            int pageNo,
            @RequestParam(value = "pageSize", defaultValue = AppConstants.DEFAULT_PAGE_SIZE, required = false)
            int pageSize,
            @RequestParam(value = "sortBy", defaultValue = AppConstants.DEFAULT_SORT_BY, required = false)
            String sortBy,
            @RequestParam(value = "sortDir", defaultValue = AppConstants.DEFAULT_SORT_DIRECTION, required = false)
            String sortDir,
            @PathVariable(name = "userId") Long userId
    ) {
        return postService.getPostsByUser(pageNo, pageSize, sortBy, sortDir, userId);
    }

    @Operation(
            description = "This endpoint get all posts bookmarked by user based on user id",
            summary = "Get Posts Bookmarked By User",

            responses = {
                    @ApiResponse(
                            description = "Success",
                            responseCode = "200"
                    )
            }
    )
    @GetMapping("/user/bookmarks/{userId}")
    public PostResponse getBookmarksByUser(
            @RequestParam(value = "pageNo", defaultValue = AppConstants.DEFAULT_PAGE_NUMBER, required = false)
            int pageNo,
            @RequestParam(value = "pageSize", defaultValue = AppConstants.DEFAULT_PAGE_SIZE, required = false)
            int pageSize,
            @RequestParam(value = "sortBy", defaultValue = AppConstants.DEFAULT_SORT_BY, required = false)
            String sortBy,
            @RequestParam(value = "sortDir", defaultValue = AppConstants.DEFAULT_SORT_DIRECTION, required = false)
            String sortDir,
            @PathVariable(name = "userId") Long userId
    ) {
        return postService.getBookmarksByUser(pageNo, pageSize, sortBy, sortDir, userId);
    }

    @Operation(
            description = "This endpoint get all posts by logged in user's followings based on user id",
            summary = "Get Posts Bookmarked By Logged In User's Followings",

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
    @GetMapping("/user/followings-posts/{userId}")
    public PostResponse getPostsByFollowings(
            @RequestParam(value = "pageNo", defaultValue = AppConstants.DEFAULT_PAGE_NUMBER, required = false)
            int pageNo,
            @RequestParam(value = "pageSize", defaultValue = AppConstants.DEFAULT_PAGE_SIZE, required = false)
            int pageSize,
            @RequestParam(value = "sortBy", defaultValue = AppConstants.DEFAULT_SORT_BY, required = false)
            String sortBy,
            @RequestParam(value = "sortDir", defaultValue = AppConstants.DEFAULT_SORT_DIRECTION, required = false)
            String sortDir,
            @PathVariable(name = "userId") Long userId,
            Authentication authentication
    ) {
        return postService.getPostsByFollowings(pageNo, pageSize, sortBy, sortDir, userId, authentication);
    }

    @Operation(
            description = "This endpoint get post based on post id",
            summary = "Get Post By Id",

            responses = {
                    @ApiResponse(
                            description = "Success",
                            responseCode = "200"
                    )
            }
    )
    @GetMapping("/{id}")
    public ResponseEntity<PostDto> getPostById(@PathVariable(name = "id") Long postId) {
        return ResponseEntity.ok(postService.getPostById(postId));
    }

    @Operation(
            description = "This endpoint lets user update post based on post id and return updated post",
            summary = "Update Post",

            responses = {
                    @ApiResponse(
                            description = "Success",
                            responseCode = "200"
                    ),
                    @ApiResponse(
                            description = "Unauthorized / Invalid Token",
                            responseCode = "403"
                    )
            },
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Post title, post content and post category",
                    required = true
            )
    )
    @PutMapping("/{id}")
    public ResponseEntity<PostDto> updatePost(
            @Valid @RequestBody PostDto postDto,
            @PathVariable(name = "id") Long postId,
            @RequestParam(value = "tags", required = false) String tags,
            Authentication authentication
    ) {
        return ResponseEntity.ok(postService.updatePost(postDto, postId, tags, authentication));
    }

    @Operation(
            description = "This endpoint lets user delete post based on post id and message",
            summary = "Delete Post",

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
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deletePost(@PathVariable(name = "id") Long postId, Authentication authentication) {
        postService.deletePostById(postId, authentication);
        return ResponseEntity.ok("Post has been deleted successfully!");
    }

    @Operation(
            description = "This endpoint get all posts which names or titles contain provided keyword",
            summary = "Search Post",

            responses = {
                    @ApiResponse(
                            description = "Success",
                            responseCode = "200"
                    )
            }
    )
    @GetMapping("/search")
    public PostResponse searchPosts(
            @RequestParam(value = "pageNo", defaultValue = AppConstants.DEFAULT_PAGE_NUMBER, required = false)
            int pageNo,
            @RequestParam(value = "pageSize", defaultValue = AppConstants.DEFAULT_PAGE_SIZE, required = false)
            int pageSize,
            @RequestParam(value = "sortBy", defaultValue = AppConstants.DEFAULT_SORT_BY, required = false)
            String sortBy,
            @RequestParam(value = "sortDir", defaultValue = AppConstants.DEFAULT_SORT_DIRECTION, required = false)
            String sortDir,
            @RequestParam(value = "keyword") String keyword
    ) {
        return postService.searchByTitleOrAuthor(pageNo, pageSize, sortBy, sortDir, keyword);
    }

    @Operation(
            description = "This endpoint lets user like or unlike(if already liked) post based on post id and return message",
            summary = "Like/Unlike Post",

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
    @PutMapping("/{postId}/like")
    public ResponseEntity<String> likePost(@PathVariable Long postId,
                         Authentication authentication) {
        String message = postService.likePost(postId, authentication);
        return ResponseEntity.ok("You just " + message + " successfully!");
    }

    @Operation(
            description = "This endpoint lets user bookmark or unbookmark(if already bookmarked) post based on post id and return message",
            summary = "Bookmark/Unbookmark Post",

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
    @PutMapping("/{postId}/bookmark")
    public ResponseEntity<String> bookmarkPost(@PathVariable Long postId,
                         Authentication authentication) {
        String message = postService.bookmarkPost(postId, authentication);
        return ResponseEntity.ok("You just " + message + " successfully!");
    }
}
