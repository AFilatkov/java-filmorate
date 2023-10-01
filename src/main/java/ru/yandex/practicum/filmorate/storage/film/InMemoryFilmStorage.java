package ru.yandex.practicum.filmorate.storage.film;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFound;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.stream.Collectors;

@Component
@Qualifier("mainFilmStorage")
public class InMemoryFilmStorage implements FilmStorage {
    private static final Logger log = LoggerFactory.getLogger(InMemoryFilmStorage.class);
    private final UserStorage userStorage;
    private HashMap<Integer, Film> films = new HashMap<>();
    private Integer currentId = 1;

    @Autowired
    public InMemoryFilmStorage(@Qualifier("mainUserStorage") UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    @Override
    public Film add(Film film) throws ValidationException {
        films.put(currentId, film);
        film.setId(currentId);
        currentId += 1;
        return film;
    }

    @Override
    public Film update(Film film) throws NotFound {
        if (films.containsKey(film.getId())) {
            films.put(film.getId(), film);
            return film;
        } else {
            throw new NotFound("Заданный идентификатор фильма не найден");
        }
    }

    @Override
    public Film remove(Film film) throws NotFound {
        if (films.containsKey(film.getId())) {
            Film result = films.get(film.getId());
            films.remove(result.getId());
            return result;
        } else {
            throw new NotFound("Заданный идентификатор не найден");
        }
    }

    @Override
    public Collection<Film> getAll() {
        return films.values();
    }

    @Override
    public Film getFilm(Integer id) throws NotFound {
        if (films.containsKey(id)) {
            return films.get(id);
        } else {
            throw new NotFound("Заданный идентификатор фильма не найден");
        }
    }

    @Override
    public boolean addLike(Integer id, Integer userId) throws NotFound {
        Film film = getFilm(id);
        User user = userStorage.getUser(userId);
        film.addLike(user);
        return true;
    }

    @Override
    public boolean removeLike(Integer id, Integer userId) throws NotFound {
        Film film = getFilm(id);
        User user = userStorage.getUser(userId);
        film.removeLike(user);
        return true;
    }

    @Override
    public Collection<Film> getMostPopularFilms(Integer count) {
        return getAll().stream()
                .sorted(Comparator.comparingInt(f -> -f.getLikes().size()))
                .limit(count)
                .collect(Collectors.toList());
    }
}
