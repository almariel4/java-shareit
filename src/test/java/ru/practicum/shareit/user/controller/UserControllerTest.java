package ru.practicum.shareit.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.model.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureJsonTesters
@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
class UserControllerTest {

    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    UserService userService;

    @Autowired
    private MockMvc mockMvc;
    private UserDto userDto;
    String header = "X-Sharer-User-Id";

    @BeforeEach
    void setUp() {
        userDto = new UserDto(1L, "Anna", "test@test.ru");
    }

    @SneakyThrows
    @Test
    void getAllUsers_whenUsersExist_thenReturnListOfUserDto() {
        when(userService.getAllUsers()).thenReturn(List.of(userDto));

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(userDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].name", is(userDto.getName())));
    }

    @SneakyThrows
    @Test
    void getUserByUserId_whenUserExists_thenReturnUserDto() {
        when(userService.getUser(anyLong())).thenReturn(userDto);

        mockMvc.perform(get("/users/{id}", 1L)
                        .header(header, 1L)
                        .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(userDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(userDto.getName())));
    }

    @SneakyThrows
    @Test
    void createUser_WhenUserIsValidated_thenReturnCreatedUser() {
        when(userService.createUser(any())).thenReturn(userDto);

        String response = mockMvc.perform(post("/users")
                        .header(header, 1L)
                        .content(objectMapper.writeValueAsString(userDto))
                        .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(userDto.getId()), Long.class))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);

        assertEquals(objectMapper.writeValueAsString(userDto), response);
        verify(userService, times(1)).createUser(any());
    }

    @SneakyThrows
    @Test
    void updateUser_whenUserExistsAndUserIsValidated_thenReturnUpdatedUserDto() {
        userService.createUser(userDto);
        userDto.setName("UpdateName");
        userDto.setEmail("update@test.ru");
        when(userService.updateUser(anyLong(), any())).thenReturn(userDto);

        String response = mockMvc.perform(patch("/users/{userId}",  1L)
                        .header(header, 1L)
                        .content(objectMapper.writeValueAsString(userDto))
                        .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(userDto.getId()), Long.class))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);

        assertEquals(objectMapper.writeValueAsString(userDto), response);
        verify(userService, times(1)).updateUser(1L, userDto);
    }

    @SneakyThrows
    @Test
    void deleteUser_whenUserExists_thenReturnStatusOk() {
        mockMvc.perform(delete("/users/{id}", 1L)
                        .header(header, 1L)
                        .contentType("application/json"))
                .andExpect(status().isOk());

        verify(userService, times(1)).deleteUser(anyLong());
    }
}