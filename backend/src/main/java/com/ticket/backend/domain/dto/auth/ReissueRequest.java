package com.ticket.backend.domain.dto.auth;

import jakarta.validation.constraints.NotBlank;

public class ReissueRequest {

    @NotBlank
    private String refreshToken;

}
