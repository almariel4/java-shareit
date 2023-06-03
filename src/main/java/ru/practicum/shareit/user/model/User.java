package ru.practicum.shareit.user.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class User {

    private Long id;
    @NotBlank
    private String name;
    @Email
    @NotBlank
    private String email;
}
