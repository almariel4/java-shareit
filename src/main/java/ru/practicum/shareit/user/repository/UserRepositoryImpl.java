package ru.practicum.shareit.user.repository;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.model.UserDto;

import javax.validation.ValidationException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class UserRepositoryImpl implements UserRepository {

    private final List<User> users = new ArrayList<>();

    @Override
    public List<User> getAllUsers() {
        return users;
    }

    @Override
    public User getUser(Long userId) {
        User user = users.stream().filter(user1 -> user1.getId().equals(userId)).findFirst().get();
        if (user != null) {
            return user;
        } else {
            throw new NotFoundException("Пользователь с id " + userId + "не найден");
        }
    }

    @Override
    public User createUser(UserDto userDto) {
        List<User> userList;
        if (userDto.getEmail() != null) {
            userList = users.stream().filter(user1 -> user1.getEmail().equals(userDto.getEmail())).collect(Collectors.toList());
        } else {
            throw new MissingValueException("Не указан e-mail пользователя");
        }
        User newUser;
        if (userList.isEmpty()) {
            newUser = UserMapper.toUser(userDto);
            users.add(newUser);
        } else {
            throw new ValidationException("Пользователь с таким e-mail уже существует");
        }
        return newUser;
    }

    @Override
    public User updateUser(Long userId, UserDto userDto) {
        User user = users.stream().filter(x -> x.getId().equals(userId)).findFirst().get();
        boolean isEmailAlreadyExist = users.stream().anyMatch(x -> x.getEmail().equals(userDto.getEmail()) && !x.getId().equals(userId));
        User updUser;
        if (user != null) {
            updUser = user;
            if (isEmailAlreadyExist) {
                throw new ValidationException("Пользователь с таким e-mail уже существует");
            }
            if (userDto.getEmail() != null) {
                updUser.setEmail(userDto.getEmail());
            } else {
                updUser.setEmail(user.getEmail());
            }
            if (userDto.getName() != null) {
                updUser.setName(userDto.getName());
            }
            users.remove(updUser);
            users.add(updUser);
        } else {
            throw new NotFoundException("Пользователь с e-mail " + userDto.getEmail() + "не найден");
        }
        return updUser;
    }

    @Override
    public void deleteUser(Long userId) {
        User user = users.stream().filter(user1 -> user1.getId().equals(userId)).findFirst().get();
        users.remove(user);
    }

}
