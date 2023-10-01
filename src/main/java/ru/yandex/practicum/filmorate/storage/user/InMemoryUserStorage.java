package ru.yandex.practicum.filmorate.storage.user;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFound;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@Qualifier("mainUserStorage")
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
    public boolean addFriend(Integer id1, Integer id2) throws NotFound {
        boolean result = true;
        User user1 = getUser(id1);
        User user2 = getUser(id2);
        user1.addFriend(user2);
        user2.addFriend(user1);
        return result;
    }

    @Override
    public boolean removeFriend(Integer id1, Integer id2) throws NotFound {
        User user1 = getUser(id1);
        User user2 = getUser(id2);
        return user1.removeFriend(user2) && user2.removeFriend(user1);
    }

    @Override
    public Collection<User> getFriends(Integer id) throws NotFound {
        return getUser(id).getFriends().stream()
                .map(i -> {
                    try {
                        return getUser(i);
                    } catch (NotFound e) {
                        throw new RuntimeException(e);
                    }
                })
                .collect(Collectors.toList());
    }

    @Override
    public Collection<User> getCommonFriends(Integer id1, Integer id2) throws NotFound {
        Set<Integer> result = new HashSet<>(getUser(id1).getFriends());
        result.retainAll(getUser(id2).getFriends());
        return result.stream().map(currentId -> {
            try {
                return getUser(currentId);
            } catch (NotFound e) {
                throw new RuntimeException(e);
            }
        }).collect(Collectors.toList());
    }

    @Override
    public Collection<User> getAll() {
        return users.values();
    }
}
