package com.hieutt.blogRESTapi.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SignInDto {
    @Email(message = "Please provide a valid email!")
    @NotEmpty(message = "Please provide an email!")
    private String email;

    @NotEmpty(message = "Please provide an password!")
    @Size(min = 4, message = "Your password should have at least 4 characters!")
    private String password;
}
