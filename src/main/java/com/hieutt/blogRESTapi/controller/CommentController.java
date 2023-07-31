package com.hieutt.blogRESTapi.controller;

import com.hieutt.blogRESTapi.dto.CommentDto;
import com.hieutt.blogRESTapi.service.CommentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class CommentController {
    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @PostMapping("/posts/{id}/comments")
    public ResponseEntity<CommentDto> createComment(
            @PathVariable(name = "id") Long postId,
            @Valid @RequestBody CommentDto commentDto,
            @RequestParam(value = "replyCmtId") Long repliedCmtId,
            Authentication authentication) {

        return new ResponseEntity<>(commentService.createComment(postId, commentDto, repliedCmtId, authentication), HttpStatus.CREATED);
    }

    @GetMapping("/posts/{id}/comments")
    public ResponseEntity<List<CommentDto>> getCommentsByPostId(@PathVariable(value = "id") Long postId) {
        return ResponseEntity.ok(commentService.getAllComments(postId));
    }

    @GetMapping("/posts/{postId}/comments/{commentId}")
    public ResponseEntity<CommentDto> getCommentById(
            @PathVariable(value = "postId") Long postId,
            @PathVariable(value = "commentId") Long commentId
    ) {
        return ResponseEntity.ok(commentService.getCommentById(postId, commentId));
    }

    @GetMapping("/posts/{postId}/reply-comments/{commentId}")
    public ResponseEntity<List<CommentDto>> getReplyComments(
            @PathVariable(value = "postId") Long postId,
            @PathVariable(value = "commentId") Long commentId
    ) {
        return ResponseEntity.ok(commentService.getReplyComments(postId, commentId));
    }

    @PutMapping("/posts/{postId}/comments/{commentId}")
    public ResponseEntity<CommentDto> updateComment(
            @PathVariable(value = "postId") Long postId,
            @PathVariable(value = "commentId") Long commentId,
            @Valid @RequestBody CommentDto commentDto,
            Authentication authentication
    ) {
        return ResponseEntity.ok(commentService.updateComment(postId, commentId, commentDto, authentication));
    }

    @DeleteMapping("/posts/{postId}/comments/{commentId}")
    public ResponseEntity<String> deleteComment(
            @PathVariable(value = "postId") Long postId,
            @PathVariable(value = "commentId") Long commentId,
            Authentication authentication
    ) {
        commentService.deleteComment(postId, commentId, authentication);
        return ResponseEntity.ok("This comment is deleted successfully!");
    }
}
