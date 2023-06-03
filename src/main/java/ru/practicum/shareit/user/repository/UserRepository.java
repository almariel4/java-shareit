package ru.practicum.shareit.user.repository;

import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.model.UserDto;

import java.util.List;

public interface UserRepository {

    List<User> getAllUsers();

    User getUser(Long userId);

    User createUser(User user);

    User updateUser(Long userId, UserDto userDto);

    void deleteUser(Long userId);

}
