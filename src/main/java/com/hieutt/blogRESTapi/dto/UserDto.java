package com.hieutt.blogRESTapi.dto;

import com.hieutt.blogRESTapi.entity.Comment;
import com.hieutt.blogRESTapi.entity.Post;
import com.hieutt.blogRESTapi.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Data
public class UserDto {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private Role role;

}
