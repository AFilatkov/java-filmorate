package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.exception.NotFound;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;

public interface UserStorage {
    User add(User user) throws ValidationException, NotFound;

    User update(User user) throws NotFound;

    User remove(User user) throws NotFound;

    User get(Integer id) throws NotFound;

    boolean addFriend(Integer id1, Integer id2) throws NotFound;

    boolean removeFriend(Integer id1, Integer id2) throws NotFound;

    Collection<User> getFriends(Integer id) throws NotFound;

    Collection<User> getCommonFriends(Integer id1, Integer id2) throws NotFound;

    Collection<User> getAll() throws NotFound;
}
