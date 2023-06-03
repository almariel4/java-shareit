package ru.practicum.shareit.user.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class UserDto {

    private Long id;
    private String name;
    @Email
    private String email;

    public UserDto(String name, String email) {
    }
}
