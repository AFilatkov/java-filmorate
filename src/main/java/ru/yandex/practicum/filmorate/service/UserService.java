package ru.yandex.practicum.filmorate.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFound;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.util.Collection;

@Service
public class UserService {
    private static final Logger log = LoggerFactory.getLogger(UserService.class);
    private final UserStorage userStorage;

    @Autowired
    public UserService(@Qualifier("testUserDb") UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public User add(User user) throws ValidationException, NotFound {
        if (validateUser(user)) {
            log.debug("Добавление пользователя - {}", user);
            return userStorage.add(user);
        } else {
            throw new ValidationException("Введены неверные данные пользователя");
        }
    }

    public Collection<User> getAll() throws NotFound {
        return userStorage.getAll();
    }

    public User get(Integer id) throws NotFound {
        try {
            return userStorage.get(id);
        } catch (DataAccessException e) {
            throw new NotFound("Указан неправильный идентификатор пользователя при получении пользователя");
        }
    }

    public User remove(User user) throws NotFound, ValidationException {
        if (validateUser(user)) {
            try {
                log.debug("Удаление пользователя - {}", user);
                return userStorage.remove(user);
            } catch (DataAccessException e) {
                throw new NotFound("Пользователь при удалении не найден");
            }
        } else {
            throw new ValidationException("Введены неверные данные пользователя");
        }
    }

    public User update(User user) throws NotFound, ValidationException {
        if (validateUser(user)) {
            try {
                log.debug("Изменение пользователя - {}", user);
                return userStorage.update(user);
            } catch (DataAccessException e) {
                throw new NotFound("Данные о пользователе не найдены");
            }
        } else {
            throw new ValidationException("Введены неверные данные пользователя");
        }
    }

    public boolean addFriend(Integer id1, Integer id2) throws NotFound {
        try {
            return userStorage.addFriend(id1, id2);
        } catch (DataAccessException e) {
            throw new NotFound("Указаны неправильные идентификаторы при добалении друзей");
        }
    }

    public boolean removeFriend(Integer id1, Integer id2) throws NotFound {
        try {
            return userStorage.removeFriend(id1, id2);
        } catch (DataAccessException e) {
            throw new NotFound("При удалении друзей указаны неправильные идентификаторы");
        }
    }

    public Collection<User> getFriends(Integer userId) throws NotFound {
        try {
            return userStorage.getFriends(userId);
        } catch (DataAccessException e) {
            throw new NotFound("При получении списка друзей указан неправильный идентификатор");
        }
    }

    public Collection<User> commonFriends(Integer id1, Integer id2) throws NotFound {
        try {
            return userStorage.getCommonFriends(id1, id2);
        } catch (DataAccessException e) {
            throw new NotFound("При получении списка общих друзей указаны неправильные идентификаторы");
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
