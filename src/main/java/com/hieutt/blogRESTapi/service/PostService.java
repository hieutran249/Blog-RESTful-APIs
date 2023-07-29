package com.hieutt.blogRESTapi.service;

import com.hieutt.blogRESTapi.dto.PostDto;
import com.hieutt.blogRESTapi.dto.PostResponse;
import org.springframework.security.core.Authentication;

import java.util.List;

public interface PostService {
    PostDto createPost(PostDto postDto, String tags, Authentication authentication);

    PostResponse getAllPosts(int pageNo, int pageSize, String sortBy, String sortDir);
    PostResponse getPostsByCategory(int pageNo, int pageSize, String sortBy, String sortDir, Long categoryId);
    PostResponse getPostsByTag(int pageNo, int pageSize, String sortBy, String sortDir, Long tagId);

    PostDto getPostById(Long id);

    PostDto updatePost(PostDto postDto, Long id, String tags);

    void deletePostById(Long id);

    List<PostDto> getPostsByUser(Long userId);
}
