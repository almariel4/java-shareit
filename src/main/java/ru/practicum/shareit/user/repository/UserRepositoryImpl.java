package ru.practicum.shareit.user.repository;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.model.UserDto;

import javax.validation.ValidationException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class UserRepositoryImpl implements UserRepository {

    private final Map<Long, User> users = new HashMap<>();
    static Long userId = 1L;

    @Override
    public List<User> getAllUsers() {
        return new ArrayList<>(users.values());
    }

    @Override
    public User getUser(Long userId) {
        if (users.containsKey(userId)) {
            return users.get(userId);
        } else {
            throw new NotFoundException("Пользователь с id " + userId + "не найден");
        }
    }

    @Override
    public User createUser(User user) {
        if (user.getEmail() == null) {
            throw new MissingValueException("Не указан e-mail пользователя");
        }
        boolean isExist = users.values().stream().anyMatch(user1 -> user1.getEmail().equals(user.getEmail()));
        if (!isExist) {
            user.setId(generateId());
            users.put(user.getId(), user);
        } else {
            throw new ValidationException("Пользователь с таким e-mail уже существует");
        }
        return user;
    }

    @Override
    public User updateUser(Long userId, UserDto userDto) {
        User user = users.get(userId);
        boolean isEmailAlreadyExist = users.values().stream().anyMatch(x -> x.getEmail().equals(userDto.getEmail()) && !x.getId().equals(userId));
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
            users.remove(userId);
            users.put(userId, updUser);
        } else {
            throw new NotFoundException("Пользователь с e-mail " + userDto.getEmail() + "не найден");
        }
        return updUser;
    }

    @Override
    public void deleteUser(Long userId) {
        users.remove(userId);
    }

    private Long generateId() {
        return userId++;
    }

}
