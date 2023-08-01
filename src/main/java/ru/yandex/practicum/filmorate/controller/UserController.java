package ru.yandex.practicum.filmorate.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.*;

@RequestMapping("/users")
@RestController
public class UserController {
    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    private HashMap<Integer, User> users = new HashMap<>();
    private int currentId = 1;

    @GetMapping
    public Collection<User> getUsers() {
        return users.values();
    }

    @PostMapping
    public User createUser(@RequestBody User user) throws ValidationException {
        if (validateUser(user)) {
            log.debug("Добавление пользователя - {}", user);
            users.put(currentId, user);
            user.setId(currentId);
            currentId += 1;
            return user;
        } else {
            throw new ValidationException("Введены неверные данные пользователя");
        }
    }

    @PutMapping
    public User updateUser(@RequestBody User user) throws ValidationException {
        if (validateUser(user)) {
            log.debug("Изменение пользователя - {}", user);
            if (users.containsKey(user.getId())) {
                users.put(user.getId(), user);
                return user;
            } else {
                throw new ValidationException("Заданный идентификатор пользователя не найден");
            }
        } else {
            return null;
        }
    }

    private boolean validateUser(User user) throws ValidationException {
        if (user.getEmail() == null
                || user.getEmail().isEmpty()
                || !user.getEmail().contains("@")) {
            String message = "Электронная почта не может быть " +
                    "пустой и должна содержать символ @";
            log.debug("Ошибка создания пользователя - {}", message);
            throw new ValidationException(message);
        }

        if (user.getLogin() == null
                || user.getLogin().isEmpty()
                || user.getLogin().contains(" ")) {
            String message = "Логин не может быть пустым и содержать пробелы";
            log.debug("Ошибка создания пользователя - {}", message);
            throw new ValidationException(message);
        }

        if (user.getName() == null || user.getName().isEmpty()) {
            user.setName(user.getLogin());
        }

        if (user.getBirthday().isAfter(LocalDate.now())) {
            String message = "Дата рождения не может быть в будущем";
            log.debug("Ошибка создания пользователя - {}", message);
            throw new ValidationException(message);
        }

        return true;
    }
}


