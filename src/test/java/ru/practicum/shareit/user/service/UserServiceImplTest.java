package ru.practicum.shareit.user.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exceptions.BadRequestException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.model.UserDto;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private UserServiceImpl userService;

    private UserDto userDto;
    private User user;

    @BeforeEach
    void setUp() {
        userDto = new UserDto(1L, "Anna", "test@test.ru");
        user = new User(1L, "Anna", "test@test.ru");
    }

    @Test
    void getAllUsers_whenUserAdded_thenReturnedListOfUserDto() {
        when(userRepository.findAll()).thenReturn(List.of(user));

        List<UserDto> userDtos = userService.getAllUsers();

        assertEquals(1, userDtos.size());
        assertEquals("Anna", userDtos.get(0).getName());
        assertEquals("test@test.ru", userDtos.get(0).getEmail());
    }

    @Test
    void getUser_whenUserExists_thenReturnUserDto() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));

        UserDto userDtoTest = userService.getUser(user.getId());

        assertEquals(1L, userDtoTest.getId());
        assertEquals("Anna", userDtoTest.getName());
        assertEquals("test@test.ru", userDtoTest.getEmail());
    }

    @Test
    void getUser_whenUserIsNotExist_thenThrowNotFoundException() {
        when(userRepository.findById(anyLong()))
                .thenThrow(new NotFoundException("Пользователь с id " + user.getId() + " не найден"));

        NotFoundException thrown = Assertions.assertThrows(NotFoundException.class, () -> {
            userService.getUser(user.getId());
        });

        assertEquals("Пользователь с id " + user.getId() + " не найден", thrown.getMessage());
    }

    @Test
    void createUser_whenUserIsValidated_thenReturnedUserDto() {
        when(userRepository.save(any())).thenReturn(user);

        UserDto userDtoTest = userService.createUser(userDto);

        assertEquals(userDto.getId(), userDtoTest.getId());
        assertEquals(userDto.getName(), userDtoTest.getName());
        assertEquals(userDto.getEmail(), userDtoTest.getEmail());
    }

    @Test
    void createUser_whenUserNameAndEmailNull_thenThrowBadRequestException() {
        userDto.setName(null);
        userDto.setEmail(null);

        BadRequestException thrown = Assertions.assertThrows(BadRequestException.class, () -> {
            userService.createUser(userDto);
        });

        assertEquals("Не указано имя и email пользователя", thrown.getMessage());
    }

    @Test
    void updateUser_whenUserExists_thenReturnedUpdatedUserDto() {
        when(userRepository.save(any())).thenReturn(user);
        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(user));

        userDto.setName("Updated Name");
        userDto.setEmail("updated@test.ru");
        UserDto userTest = userService.updateUser(userDto.getId(), userDto);

        assertEquals(1L, userTest.getId());
        assertEquals("Updated Name", userTest.getName());
        assertEquals("updated@test.ru", userTest.getEmail());
    }

    @Test
    void updateUser_whenUserNameNull_thenReturnedUpdatedUserDtoWithOldName() {
        UserDto userDtoInit = new UserDto(1L, null, "updated@test.ru");
        when(userRepository.save(any())).thenReturn(user);
        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(user));

        UserDto userTest = userService.updateUser(userDto.getId(), userDtoInit);

        assertEquals(1L, userTest.getId());
        assertEquals("Anna", userTest.getName());
        assertEquals("updated@test.ru", userTest.getEmail());
    }

    @Test
    void updateUser_whenUserEmailNull_thenReturnedUpdatedUserDtoWithOldEmail() {
        UserDto userDtoInit = new UserDto(1L, "Anna", null);
        when(userRepository.save(user)).thenReturn(user);
        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(user));

        UserDto userTest = userService.updateUser(userDto.getId(), userDtoInit);

        assertEquals(1L, userTest.getId());
        assertEquals("Anna", userTest.getName());
        assertEquals("test@test.ru", userTest.getEmail());
    }

    @Test
    void deleteUser_whenUserExists_thenReturnNullObject() {
        when(userRepository.save(any())).thenReturn(user);

        UserDto userDtoTest = userService.createUser(userDto);
        assertEquals(userDto.getId(), userDtoTest.getId());

        userService.deleteUser(userDtoTest.getId());
        NotFoundException thrown = Assertions.assertThrows(NotFoundException.class, () -> {
            userService.getUser(userDtoTest.getId());
        });

        assertEquals("Пользователь с id " + user.getId() + " не найден", thrown.getMessage());
    }
}