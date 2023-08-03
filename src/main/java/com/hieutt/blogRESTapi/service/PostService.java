package com.hieutt.blogRESTapi.service;

import com.hieutt.blogRESTapi.dto.PostDto;
import com.hieutt.blogRESTapi.dto.PostResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.Authentication;

import java.util.List;

public interface PostService {
    PostDto createPost(PostDto postDto, String tags, Authentication authentication);
    PostResponse getAllPosts(int pageNo, int pageSize, String sortBy, String sortDir);
    PostResponse getPostsByCategory(int pageNo, int pageSize, String sortBy, String sortDir, Long categoryId);
    PostResponse getPostsByTag(int pageNo, int pageSize, String sortBy, String sortDir, Long tagId);
    PostResponse getPostsByUser(int pageNo, int pageSize, String sortBy, String sortDir, Long userId);
    PostResponse getBookmarksByUser(int pageNo, int pageSize, String sortBy, String sortDir, Long userId);
    PostResponse getPostsByFollowings(int pageNo, int pageSize, String sortBy, String sortDir, Long userId, Authentication authentication);
    PostDto getPostById(Long id);
    PostDto updatePost(PostDto postDto, Long id, String tags, Authentication authentication);
    void deletePostById(Long postId, Authentication authentication);
    PostResponse searchByTitleOrAuthor(int pageNo, int pageSize, String sortBy, String sortDir, String keyword);
    String likePost(Long postId, Authentication authentication);
    String bookmarkPost(Long postId, Authentication authentication);
}
