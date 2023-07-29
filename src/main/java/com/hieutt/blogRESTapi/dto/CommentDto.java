package com.hieutt.blogRESTapi.dto;

import com.hieutt.blogRESTapi.entity.Comment;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CommentDto {
    private Long id;

    @NotEmpty
    @Size(min = 10, message = "Comment body must minimum 10 characters")
    private String content;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private int vote;
    private PostDto post;
    private UserDto author;
    private Comment replyToComment;

}
