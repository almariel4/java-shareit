package ru.practicum.shareit.user.controller;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.model.UserDto;
import ru.practicum.shareit.user.service.UserService;

import javax.validation.Valid;
import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequestMapping(path = "/users")
@AllArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/{userId}")
    public User getUser(@PathVariable("userId") Long userId) {
        return userService.getUser(userId);
    }

    @PostMapping
    public User createUser(@Valid @RequestBody UserDto userDto) {
        return userService.createUser(userDto);
    }

    @PatchMapping("/{userId}")
    public User updateUser(@PathVariable("userId") Long userId, @Valid @RequestBody UserDto userDto) {
        return userService.updateUser(userId, userDto);
    }

    @DeleteMapping("/{userId}")
    public void deleteUser(@PathVariable("userId") Long userId) {
        userService.deleteUser(userId);
    }


}
