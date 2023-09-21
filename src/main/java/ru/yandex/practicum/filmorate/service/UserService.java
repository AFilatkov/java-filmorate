package ru.yandex.practicum.filmorate.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFound;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserService {
    private static final Logger log = LoggerFactory.getLogger(UserService.class);
    private final UserStorage userStorage;

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public User add(User user) throws ValidationException {
        if (validateUser(user)) {
            log.debug("Добавление пользователя - {}", user);
            return userStorage.add(user);
        } else {
            throw new ValidationException("Введены неверные данные пользователя");
        }
    }

    public Collection<User> getAll() {
        return userStorage.getAll();
    }

    public User getUser(Integer id) throws NotFound {
        return userStorage.getUser(id);
    }

    public User remove(User user) throws NotFound, ValidationException {
        if (validateUser(user)) {
            log.debug("Удаление фильма - {}", user);
            return userStorage.remove(user);
        } else {
            throw new ValidationException("Введены неверные данные пользователя");
        }
    }

    public User update(User user) throws NotFound, ValidationException {
        if (validateUser(user)) {
            log.debug("Изменение пользователя - {}", user);
            return userStorage.update(user);
        } else {
            throw new ValidationException("Введены неверные данные пользователя");
        }
    }

    public boolean addFriend(Integer id1, Integer id2) throws NotFound {
        boolean result = true;
        User user1 = userStorage.getUser(id1);
        User user2 = userStorage.getUser(id2);
        user1.addFriend(user2);
        user2.addFriend(user1);

        return result;
    }

    public boolean removeFriend(Integer id1, Integer id2) throws NotFound {
        User user1 = userStorage.getUser(id1);
        User user2 = userStorage.getUser(id2);
        return user1.removeFriend(user2) && user2.removeFriend(user1);
    }

    public List<User> getFriends(Integer userId) throws NotFound {
        return userStorage.getUser(userId).getFriends().stream()
                .map(i -> {
                    try {
                        return userStorage.getUser(i);
                    } catch (NotFound e) {
                        throw new RuntimeException(e);
                    }
                })
                .collect(Collectors.toList());
    }

    public List<User> commonFriends(Integer id1, Integer id2) throws NotFound {
        Set<Integer> result = new HashSet<>(userStorage.getUser(id1).getFriends());
        result.retainAll(userStorage.getUser(id2).getFriends());
        return result.stream().map(currentId -> {
            try {
                return userStorage.getUser(currentId);
            } catch (NotFound e) {
                throw new RuntimeException(e);
            }
        }).collect(Collectors.toList());
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
