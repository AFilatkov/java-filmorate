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

    public boolean addLike(Integer userId) {
        likes.add(userId);
        return true;
    }

    public boolean removeLike(Integer userId) {
        return likes.remove(userId);
    }

    public Set<Integer> getLikes() {
        return likes;
    }
}
