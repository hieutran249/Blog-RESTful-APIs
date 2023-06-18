package com.hieutt.blogRESTapi.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CommentDto {
    private Long id;

    @NotEmpty
    @Size(min = 10, message = "Comment body must minimum 10 characters")
    private String body;
    private UserResponse user;

}
