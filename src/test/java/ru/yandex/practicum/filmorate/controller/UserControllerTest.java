package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import java.time.LocalDate;

class UserControllerTest {
    private UserController userController;

    @BeforeEach
    void createController() {
        userController = new UserController();
    }

    @Test
    void createUserWithEmptyEmail() {
        User user = new User();
        user.setId(1);
        user.setEmail("");
        user.setLogin("login");
        user.setName("name");
        user.setBirthday(LocalDate.of(1997, 1, 17));

        ValidationException exception = Assertions.assertThrows(ValidationException.class, () -> userController.createUser(user));

        Assertions.assertEquals("Электронная почта не может быть пустой и должна содержать символ @", exception.getMessage());
        Assertions.assertEquals(0, userController.getUsers().size());
    }

    @Test
    void createUserWithErrorLogin() {
        User user = new User();
        user.setId(1);
        user.setEmail("email@gogle.com");
        user.setLogin("login with space");
        user.setName("name");
        user.setBirthday(LocalDate.of(1997, 1, 17));

        ValidationException exception = Assertions.assertThrows(ValidationException.class, () -> userController.createUser(user));

        Assertions.assertEquals("Логин не может быть пустым и содержать пробелы", exception.getMessage());
        Assertions.assertEquals(0, userController.getUsers().size());
    }

    @Test
    void createUserWithFutureBirthday() {
        User user = new User();
        user.setId(1);
        user.setEmail("email@gogle.com");
        user.setLogin("login");
        user.setName("name");
        user.setBirthday(LocalDate.of(2097, 1, 17));

        ValidationException exception = Assertions.assertThrows(ValidationException.class, () -> userController.createUser(user));

        Assertions.assertEquals("Дата рождения не может быть в будущем", exception.getMessage());
        Assertions.assertEquals(0, userController.getUsers().size());
    }
}