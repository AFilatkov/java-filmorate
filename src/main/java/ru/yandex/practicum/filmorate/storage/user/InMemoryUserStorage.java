package ru.yandex.practicum.filmorate.storage.user;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFound;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import java.util.Collection;
import java.util.HashMap;

@Component
public class InMemoryUserStorage implements UserStorage {
    private static final Logger log = LoggerFactory.getLogger(InMemoryUserStorage.class);
    private HashMap<Integer, User> users = new HashMap<>();
    private Integer currentId = 1;

    @Override
    public User getUser(Integer id) throws NotFound {
        if (users.containsKey(id)) {
            return users.get(id);
        } else {
            throw new NotFound("Заданный идентификатор не найден");
        }
    }

    @Override
    public User add(User user) throws ValidationException {
        users.put(currentId, user);
        user.setId(currentId);
        currentId += 1;
        return user;
    }

    @Override
    public User update(User user) throws NotFound {
        if (users.containsKey(user.getId())) {
            users.put(user.getId(), user);
        } else {
            throw new NotFound("Пользователь с заданым id не найден");
        }
        return user;
    }

    @Override
    public User remove(User user) throws NotFound {
        if (users.containsKey(user.getId())) {
            users.remove(user.getId());
        } else {
            throw new NotFound("Пользователь с заданым id не найден");
        }
        return user;
    }

    @Override
    public Collection<User> getAll() {
        return users.values();
    }
}
