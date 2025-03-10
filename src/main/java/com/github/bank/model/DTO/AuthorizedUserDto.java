package com.github.bank.model.DTO;

import com.github.bank.util.Enum.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AuthorizedUserDto {
    // TODO: are these fields ok ? can we make email as unique? if we need to update passing AuthorizedUser ID might not appropiate
    @NotBlank
    private String fullName;
    @NotBlank
    @Email
    private String email;
    @NotNull
    private Role role;
}
