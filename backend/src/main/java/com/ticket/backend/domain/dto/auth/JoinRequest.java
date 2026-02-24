package com.ticket.backend.domain.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class JoinRequest {

    @NotEmpty
    private String username;

    @NotEmpty
    @Size(min = 8, max = 20, message = "비밀번호는 8자 이상 20자 이하로 작성해주세요.")
    private String password;

    @NotEmpty
    @Email
    private String email;

}
