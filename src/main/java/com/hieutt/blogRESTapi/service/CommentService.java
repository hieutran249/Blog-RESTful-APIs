package com.hieutt.blogRESTapi.service;

import com.hieutt.blogRESTapi.dto.CommentDto;
import org.springframework.security.core.Authentication;

import java.util.List;

public interface CommentService {
    CommentDto createComment(Long postId, CommentDto commentDto, Authentication authentication);

    List<CommentDto> getAllComments(Long postId);

    CommentDto getCommentById(Long postId, Long commentId);

    CommentDto updateComment(Long postId, Long commentId, CommentDto commentDto);

    void deleteComment(Long postId, Long commentId);
}
