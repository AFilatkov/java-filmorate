package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import java.time.LocalDate;
import java.util.*;

@Data
public class User {
    private int id;
    private String email;
    private String login;
    private String name;
    private LocalDate birthday;

    private Set<Integer> likedFilms = new HashSet<>();
    private Set<Integer> friends = new LinkedHashSet<>();

    public Set<Integer> getFriends() {
        return friends;
    }

    public User addFriend(User user) {
        friends.add(user.getId());
        return user;
    }

    public boolean removeFriend(User user) {
        return friends.remove(user.getId());
    }
}
