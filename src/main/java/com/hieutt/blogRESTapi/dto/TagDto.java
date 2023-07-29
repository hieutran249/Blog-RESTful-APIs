package com.hieutt.blogRESTapi.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TagDto {
    private Long id;

    @NotEmpty
    @Size(min = 3, message = "Tag name should have at least 3 characters!")
    private String name;
    private int quantity;
}
