package com.hieutt.blogRESTapi.dto;

import com.hieutt.blogRESTapi.entity.Category;
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
public class PostDto {
    private Long id;

    @NotEmpty
    @Size(min = 2, message = "Post title should have at least 2 characters")
    private String title;

    @NotEmpty
    private String content;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private int view;
    private int vote;
    private UserDto author;
    private CategoryDto category;
}
