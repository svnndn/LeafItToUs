package ru.litu.main_service.user.dto;

import jakarta.persistence.Transient;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NewUserDto {

    @NotNull
    @NotBlank
    private String name;

    @NotNull
    private String username;

    @NotNull
    @NotBlank
    @Email
    private String email;

    @NotNull
    @Size(min = 5)
    private String password;

    @NotNull
    @Transient
    private String passwordConfirm;
}