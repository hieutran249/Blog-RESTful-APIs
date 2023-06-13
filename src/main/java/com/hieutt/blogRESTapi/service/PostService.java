package com.hieutt.blogRESTapi.service;

import com.hieutt.blogRESTapi.dto.PostDto;
import com.hieutt.blogRESTapi.dto.PostResponse;
import org.springframework.security.core.Authentication;

public interface PostService {
    PostDto createPost(PostDto postDto, String categories, Authentication authentication);

    PostResponse getAllPosts(int pageNo, int pageSize, String sortBy, String sortDir);

    PostDto getPostById(Long id);

    PostDto updatePost(PostDto postDto, Long id, String categories);

    void deletePostById(Long id);
}
