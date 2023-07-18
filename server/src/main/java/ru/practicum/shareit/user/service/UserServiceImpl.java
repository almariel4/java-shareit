package ru.practicum.shareit.user.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exceptions.BadRequestException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.model.UserDto;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {

    private UserRepository userRepository;

    @Transactional(readOnly = true)
    @Override
    public List<UserDto> getAllUsers() {
        List<User> users = userRepository.findAll();
        return users.stream().map(UserMapper::mapToUserDto).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    @Override
    public UserDto getUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + userId + " не найден"));
        return UserMapper.mapToUserDto(user);
    }

    @Transactional
    @Override
    public UserDto createUser(UserDto userDto) {
        User user;
        if (userDto.getName() != null && userDto.getEmail() != null) {
            user = userRepository.save(UserMapper.mapToUser(userDto));
        } else {
            throw new BadRequestException("Не указано имя и email пользователя");
        }
        return UserMapper.mapToUserDto(user);
    }

    @Transactional
    @Override
    public UserDto updateUser(Long userId, UserDto userDto) {
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        if (userDto.getName() != null) {
            user.setName(userDto.getName());
        }
        if (userDto.getEmail() != null) {
            user.setEmail(userDto.getEmail());
        }
        return UserMapper.mapToUserDto(userRepository.save(user));
    }

    @Transactional
    @Override
    public void deleteUser(Long userId) {
        User user = userRepository.getReferenceById(userId);
        userRepository.delete(user);
    }
}
