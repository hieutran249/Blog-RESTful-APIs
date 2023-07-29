package com.hieutt.blogRESTapi.controller;

import com.hieutt.blogRESTapi.dto.PostDto;
import com.hieutt.blogRESTapi.dto.PostResponse;
import com.hieutt.blogRESTapi.service.PostService;
import com.hieutt.blogRESTapi.utils.AppConstants;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/posts")
public class PostController {
    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    @PostMapping
    public ResponseEntity<PostDto> createPost(@Valid @RequestBody PostDto postDto,
                                              @RequestParam(value = "tags") String tags,
                                              Authentication authentication) {
        return new ResponseEntity<>(postService.createPost(postDto, tags, authentication), HttpStatus.CREATED);
    }

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

    @GetMapping("/{id}")
    public ResponseEntity<PostDto> getPostById(@PathVariable(name = "id") Long postId) {
        return ResponseEntity.ok(postService.getPostById(postId));
    }

    @GetMapping("/user/{id}")
    public ResponseEntity<List<PostDto>> getPostsByUser(@PathVariable(name = "id") Long userId) {
        return ResponseEntity.ok(postService.getPostsByUser(userId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PostDto> updatePost(
            @Valid @RequestBody PostDto postDto,
            @PathVariable(name = "id") Long postId,
            @RequestParam(value = "tags") String tags) {
        return ResponseEntity.ok(postService.updatePost(postDto, postId, tags));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deletePost(@PathVariable(name = "id") Long postId) {
        postService.deletePostById(postId);
        return ResponseEntity.ok("Post has been deleted successfully!");
    }
}
