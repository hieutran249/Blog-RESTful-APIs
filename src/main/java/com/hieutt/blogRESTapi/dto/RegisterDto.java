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
public class RegisterDto {
    @NotEmpty
    @Size(min = 2, message = "Your firstname should have at least 2 characters!")
    private String firstName;

    @NotEmpty
    @Size(min = 2, message = "Your firstname should have at least 2 characters!")
    private String lastName;

    @NotEmpty(message = "Please provide an email!")
    @Email(message = "Please provide a valid email!")
    private String email;

    @NotEmpty(message = "Please provide an password!")
    @Size(min = 4, message = "Your password should have at least 4 characters!")
    private String password;

    @NotEmpty(message = "Please confirm your password!")
    private String confirmPassword;
}
