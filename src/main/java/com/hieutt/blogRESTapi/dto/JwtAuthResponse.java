package com.hieutt.blogRESTapi.dto;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class JwtAuthResponse {
    private String token;
    private final String tokenType = "Bearer";
}
