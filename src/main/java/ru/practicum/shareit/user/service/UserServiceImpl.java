package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.model.UserDto;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public List<User> getAllUsers() {
        return userRepository.getAllUsers();
    }

    @Override
    public User getUser(Long userId) {
        return userRepository.getUser(userId);
    }

    @Override
    public User createUser(UserDto userDto) {
        return userRepository.createUser(userDto);
    }

    @Override
    public User updateUser(Long userId, UserDto userDto) {
        return userRepository.updateUser(userId, userDto);
    }

    @Override
    public void deleteUser(Long userId) {
        userRepository.deleteUser(userId);
    }
}
