package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.model.UserDto;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertThrows;


@Transactional
@SpringBootTest(
        properties = "db.name=test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class UserServiceImplItTest {

    private final UserService userService;
    private final EntityManager em;
    private UserDto userDto;
    private UserDto userDto_Kris;

    @BeforeEach
    void setUp() {
        userDto = new UserDto(1L, "Anna", "test@test.ru");
        userDto_Kris = new UserDto(2L, "Kristina", "testKristina@test.ru");
    }

    @Test
    void getAllUsers() {
        userService.createUser(userDto);
        userService.createUser(userDto_Kris);

        List<UserDto> users = userService.getAllUsers();

        assertThat(users.get(0).getId(), equalTo(userDto.getId()));
        assertThat(users.get(0).getName(), equalTo(userDto.getName()));
        assertThat(users.get(0).getEmail(), equalTo(userDto.getEmail()));
        assertThat(users.get(1).getId(), equalTo(userDto_Kris.getId()));
        assertThat(users.get(1).getName(), equalTo(userDto_Kris.getName()));
        assertThat(users.get(1).getEmail(), equalTo(userDto_Kris.getEmail()));
    }

    @Test
    void getUser() {
        userService.createUser(userDto);
        UserDto userDtoTest = userService.getUser(1L);

        assertThat(userDtoTest.getId(), equalTo(userDto.getId()));
        assertThat(userDtoTest.getName(), equalTo(userDto.getName()));
        assertThat(userDtoTest.getEmail(), equalTo(userDto.getEmail()));
    }

    @Test
    void createUser() {
        userService.createUser(userDto);

        TypedQuery<User> query = em.createQuery("select u from User u where u.email = :email", User.class);
        User user = query.setParameter("email", userDto.getEmail()).getSingleResult();

        assertThat(user.getId(), notNullValue());
        assertThat(user.getName(), equalTo(userDto.getName()));
        assertThat(user.getEmail(), equalTo(userDto.getEmail()));
    }

    @Test
    void updateUser() {
        userService.createUser(userDto);
        userDto.setName("Updated Name");
        userDto.setEmail("update@test.ru");
        userService.updateUser(1L, userDto);

        TypedQuery<User> query = em.createQuery("select u from User u where u.id = :id", User.class);
        User user = query.setParameter("id", userDto.getId()).getSingleResult();

        assertThat(user.getId(), equalTo(userDto.getId()));
        assertThat(user.getName(), equalTo(userDto.getName()));
        assertThat(user.getEmail(), equalTo(userDto.getEmail()));
    }

    @Test
    void deleteUser() {
        UserDto userDtoTest = userService.createUser(userDto);
        assertThat(userDtoTest.getId(), equalTo(userDto.getId()));

        userService.deleteUser(userDto.getId());

        assertThrows(NotFoundException.class, () -> userService.getUser(userDto.getId()));
    }
}