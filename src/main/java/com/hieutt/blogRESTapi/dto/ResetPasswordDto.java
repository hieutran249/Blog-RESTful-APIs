package com.hieutt.blogRESTapi.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ResetPasswordDto {
    @NotEmpty(message = "Please provide a new password!")
    @Size(min = 4, message = "Your password should have at least 4 characters!")
    private String newPassword;

    @NotEmpty(message = "Please confirm your password!")
    private String confirmPassword;
}
