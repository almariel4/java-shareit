package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.model.UserDto;

import java.util.List;


public interface UserService {

    List<User> getAllUsers();

    User getUser(Long userId);

    User createUser(UserDto userDto);

    User updateUser(Long userId, UserDto userDto);

    void deleteUser(Long userId);

}
