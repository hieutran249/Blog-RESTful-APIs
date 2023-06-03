package com.hieutt.blogRESTapi.service;

import com.hieutt.blogRESTapi.dto.PostDto;
import com.hieutt.blogRESTapi.dto.PostResponse;

public interface PostService {
    PostDto createPost(PostDto postDto, String categories);

    PostResponse getAllPosts(int pageNo, int pageSize, String sortBy, String sortDir);

    PostDto getPostById(Long id);

    PostDto updatePost(PostDto postDto, Long id, String categories);

    void deletePostById(Long id);
}
