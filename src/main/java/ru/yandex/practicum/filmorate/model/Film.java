package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import java.time.LocalDate;
import java.util.*;

@Data
public class Film {
    private int id;
    private String name;
    private String description;
    private LocalDate releaseDate;
    private long duration;
    private Mpa mpa;

    private Set<Genre> genres = new TreeSet<>(Comparator.comparingInt(Genre::getId));
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
