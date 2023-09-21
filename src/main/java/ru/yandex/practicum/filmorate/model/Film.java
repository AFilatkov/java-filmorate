package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
public class Film {
    private int id;
    private String name;
    private String description;
    private LocalDate releaseDate;
    private long duration;

    private Set<Integer> likes = new HashSet<>();

    public boolean addLike(User user) {
        likes.add(user.getId());
        return true;
    }

    public boolean removeLike(User user) {
        return likes.remove(user.getId());
    }

    public Set<Integer> getLikes() {
        return likes;
    }
}
