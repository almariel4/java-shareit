package ru.practicum.shareit.user.mapper;

import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.model.UserDto;

public class UserMapper {

    public static UserDto toUserDto(Long userId, User user) {
        return UserDto.builder()
                .name(user.getName())
                .email(user.getEmail())
                .build();
    }

    public static User toUser(UserDto userDto) {
        return User.builder()
//                .id(userId)
                .name(userDto.getName())
                .email(userDto.getEmail())
                .build();
    }

}
