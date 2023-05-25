package ru.practicum.shareit.user.mapper;

import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.model.UserDto;

public class UserMapper {

    static Long userId = 1L;

    private static Long generateId() {
        return userId++;
    }

    public static UserDto toUserDto(User user) {
        return new UserDto(
                user.getName(),
                user.getEmail()
        );
    }

    public static User toUser(UserDto userDto) {
        return new User(
                generateId(),
                userDto.getName(),
                userDto.getEmail()
        );
    }

}
