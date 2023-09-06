package ru.yandex.practicum.filmorate.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFound;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.film.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private static final Logger log = LoggerFactory.getLogger(FilmService.class);

    @Autowired
    public FilmService(FilmStorage filmStorage, UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    public Film add(Film film) throws ValidationException {
        if (validateFilm(film)) {
            log.debug("Добавлен фильм - {}", film);
            filmStorage.add(film);
        }
        return film;
    }

    public Film getFilm(Integer id) throws NotFound {
        return filmStorage.getFilm(id);
    }

    public Film update(Film film) throws ValidationException, NotFound {
        if (validateFilm(film)) {
            log.debug("Изменен фильм - {}", film);
            filmStorage.update(film);
        }
        return film;
    }

    public Film remove(Film film) throws NotFound, ValidationException {
        if (validateFilm(film)) {
            log.debug("Удаление фильма - {}", film);
            filmStorage.remove(film);
        }
        return film;
    }

    public Collection<Film> getAll() {
        return filmStorage.getAll();
    }

    public Film addLike(Integer filmId, Integer userId) throws NotFound {
        Film film = filmStorage.getFilm(filmId);
        User user = userStorage.getUser(userId);
        film.addLike(user);
        return film;
    }

    public Film removeLike(Integer filmId, Integer userId) throws NotFound {
        Film film = filmStorage.getFilm(filmId);
        User user = userStorage.getUser(userId);
        film.removeLike(user);
        return film;
    }

    public List<Film> getMostPopularFilms(Integer count) {
        return filmStorage.getAll().stream()
                .sorted(Comparator.comparingInt(f -> -f.getLikes().size()))
                .limit(count)
                .collect(Collectors.toList());
    }

    private boolean validateFilm(Film film) throws ValidationException {
        if (film.getName() == null || film.getName().isEmpty()) {
            String message = "Название фильма не может быть пустым";
            log.debug("Ошибка создания фильма - {}", film);
            throw new ValidationException(message);
        }

        if (film.getDescription().length() > 200) {
            String message = "Максимальная длина описания - 200 символов";
            log.debug("Ошибка создания фильма - {}", film);
            throw new ValidationException(message);
        }

        if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            String message = "Дата релиза - не раньше 28 декабря 1895 года";
            log.debug("Ошибка создания фильма - {}", film);
            throw new ValidationException(message);
        }

        if (film.getDuration() <= 0) {
            String message = "Продолжительность фильма должна быть положительной";
            log.debug("Ошибка создания фильма - {}", film);
            throw new ValidationException(message);
        }

        return true;
    }
}

