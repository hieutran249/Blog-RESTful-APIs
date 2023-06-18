package com.hieutt.blogRESTapi.dto;

import lombok.Data;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
public class UserResponse {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
//    private List<FormattedPost> posts;
}
