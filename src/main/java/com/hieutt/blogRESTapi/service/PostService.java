package com.hieutt.blogRESTapi.service;

import com.hieutt.blogRESTapi.dto.FormattedPost;
import com.hieutt.blogRESTapi.dto.PostDto;
import com.hieutt.blogRESTapi.dto.PostResponse;
import org.springframework.security.core.Authentication;

import java.util.List;

public interface PostService {
    PostDto createPost(PostDto postDto, String categories, Authentication authentication);

    PostResponse getAllPosts(int pageNo, int pageSize, String sortBy, String sortDir, String categories);

    PostDto getPostById(Long id);

    PostDto updatePost(PostDto postDto, Long id, String categories);

    void deletePostById(Long id);

    List<FormattedPost> getPostsByUser(Long userId);
}
