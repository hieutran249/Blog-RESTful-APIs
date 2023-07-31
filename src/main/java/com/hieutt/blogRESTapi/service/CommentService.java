package com.hieutt.blogRESTapi.service;

import com.hieutt.blogRESTapi.dto.CommentDto;
import org.springframework.security.core.Authentication;

import java.util.List;

public interface CommentService {
    CommentDto createComment(Long postId, CommentDto commentDto, Long repliedCmtId, Authentication authentication);

    List<CommentDto> getAllComments(Long postId);

    CommentDto getCommentById(Long postId, Long commentId);

    List<CommentDto> getReplyComments(Long postId, Long commentId);

    CommentDto updateComment(Long postId, Long commentId, CommentDto commentDto, Authentication authentication);

    void deleteComment(Long postId, Long commentId, Authentication authentication);

}
