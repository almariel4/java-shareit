package ru.practicum.shareit.user.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

/**
 * TODO Sprint add-controllers.
 */
/*@Data
@AllArgsConstructor*/
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

    private Long id;
    @NotBlank
    private String name;
    @Email
    @NotBlank
    private String email;
}
