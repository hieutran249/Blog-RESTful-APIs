package com.hieutt.blogRESTapi.controller;

import com.hieutt.blogRESTapi.dto.CommentDto;
import com.hieutt.blogRESTapi.service.CommentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Comment")
public class CommentController {
    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @Operation(
            description = "This endpoint lets user create comment based on post id and return newly created comment",
            summary = "Create Comment",

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
                    description = "Comment content",
                    required = true
            )
    )
    @PostMapping("/posts/{id}/comments")
    public ResponseEntity<CommentDto> createComment(
            @PathVariable(name = "id") Long postId,
            @Valid @RequestBody CommentDto commentDto,
            @RequestParam(value = "replyCmtId", required = false) Long repliedCmtId,
            Authentication authentication) {

        return new ResponseEntity<>(commentService.createComment(postId, commentDto, repliedCmtId, authentication), HttpStatus.CREATED);
    }

    @Operation(
            description = "This endpoint gets all comments based on post id",
            summary = "Get Comments By Post",

            responses = {
                    @ApiResponse(
                            description = "Success",
                            responseCode = "200"
                    )
            }
    )
    @GetMapping("/posts/{id}/comments")
    public ResponseEntity<List<CommentDto>> getCommentsByPost(@PathVariable(value = "id") Long postId) {
        return ResponseEntity.ok(commentService.getAllComments(postId));
    }

    @Operation(
            description = "This endpoint gets comment based on post id and comment id",
            summary = "Get Comment By Id",

            responses = {
                    @ApiResponse(
                            description = "Success",
                            responseCode = "200"
                    )
            }
    )
    @GetMapping("/posts/{postId}/comments/{commentId}")
    public ResponseEntity<CommentDto> getCommentById(
            @PathVariable(value = "postId") Long postId,
            @PathVariable(value = "commentId") Long commentId
    ) {
        return ResponseEntity.ok(commentService.getCommentById(postId, commentId));
    }

    @Operation(
            description = "This endpoint gets reply comments of a comment based on post id and comment id",
            summary = "Get Reply Comments of Comment",

            responses = {
                    @ApiResponse(
                            description = "Success",
                            responseCode = "200"
                    )
            }
    )
    @GetMapping("/posts/{postId}/reply-comments/{commentId}")
    public ResponseEntity<List<CommentDto>> getReplyComments(
            @PathVariable(value = "postId") Long postId,
            @PathVariable(value = "commentId") Long commentId
    ) {
        return ResponseEntity.ok(commentService.getReplyComments(postId, commentId));
    }

    @Operation(
            description = "This endpoint lets user update comment based on post id and return updated comment",
            summary = "Update Comment",

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
    @PutMapping("/posts/{postId}/comments/{commentId}")
    public ResponseEntity<CommentDto> updateComment(
            @PathVariable(value = "postId") Long postId,
            @PathVariable(value = "commentId") Long commentId,
            @Valid @RequestBody CommentDto commentDto,
            Authentication authentication
    ) {
        return ResponseEntity.ok(commentService.updateComment(postId, commentId, commentDto, authentication));
    }

    @Operation(
            description = "This endpoint lets user delete comment based on post id and return message",
            summary = "Delete Comment",

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
