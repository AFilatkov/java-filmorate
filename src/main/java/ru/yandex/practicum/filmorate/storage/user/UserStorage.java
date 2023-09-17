package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.exception.NotFound;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;

public interface UserStorage {
    User add(User user) throws ValidationException;

    User update(User user) throws NotFound;
    
    User remove(User user) throws NotFound;

    User getUser(Integer id) throws NotFound;

    Collection<User> getAll();
}
