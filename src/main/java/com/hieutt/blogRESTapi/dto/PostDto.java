package com.hieutt.blogRESTapi.dto;

import com.hieutt.blogRESTapi.entity.Category;
import com.hieutt.blogRESTapi.entity.User;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;
import java.util.Set;

@Data
public class PostDto {
    private Long id;

    @NotEmpty
    @Size(min = 2, message = "Post title should have at least 2 characters")
    private String title;

    @NotEmpty
    private String content;
    private UserResponse user;
    private List<CommentDto> comments;
    private List<Category> categories;
}
