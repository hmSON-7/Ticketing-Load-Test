package com.ticket.backend.domain.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LoginResponse {

    @NotBlank
    private String tokenType;

    @NotBlank
    private String accessToken;

    @NotBlank
    private Long accessExpiresIn;

    @NotBlank
    private String refreshToken;

    @NotBlank
    private Long refreshExpiresIn;

}
