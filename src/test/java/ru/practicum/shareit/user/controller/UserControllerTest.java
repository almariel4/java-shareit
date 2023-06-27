package ru.practicum.shareit.user.controller;

import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.shareit.user.model.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    UserService userService;
    @InjectMocks
    UserController userController;

    private MockMvc mockMvc;
    private UserDto userDto;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(userController)
                .build();

        userDto = new UserDto(1L, "Anna", "test@test.ru");
    }

    @SneakyThrows
    @Test
    void getAllUsers_whenUsersExist_thenReturnListOfUserDto() {

        when(userService.getAllUsers()).thenReturn(List.of(userDto));

        mockMvc.perform(get("/users")
                        .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(userDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].name", is(userDto.getName())));
    }

    @SneakyThrows
    @Test
    void getUserByUserId_whenUserExists_thenReturnUserDto() {

        when(userService.getUser(anyLong())).thenReturn(userDto);

        mockMvc.perform(get("/users/{id}", 1L)
                        .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(userDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(userDto.getName())));
    }

    @SneakyThrows
    @Test
    void createUser_WhenUserIsValidated_thenReturnCreatedUser() {

        when(userService.createUser(any())).thenReturn(userDto);

        mockMvc.perform(post("/users", userDto)
                        .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$.id", is(userDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(userDto.getName())));

        verify(userService, times(1)).createUser(userDto);
    }

    @SneakyThrows
    @Test
    void updateUser_whenUserExistsAndUserIsValidated_thenReturnUpdatedUserDto() {

        userDto.setName("UpdateName");
        userDto.setEmail("update@test.ru");
        when(userService.updateUser(anyLong(), any())).thenReturn(userDto);

        mockMvc.perform(patch("/{userId}",  1L, userDto)
                        .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(userDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].name", is(userDto.getName())));

        verify(userService, times(1)).updateUser(1L, userDto);
    }

    @SneakyThrows
    @Test
    void deleteUser_whenUserExists_thenReturnStatusOk() {

        mockMvc.perform(delete("/users/{id}", 1L)
                        .contentType("application/json"))
                .andExpect(status().isOk());

        verify(userService, times(1)).deleteUser(1L);
    }
}