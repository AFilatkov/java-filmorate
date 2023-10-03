package ru.yandex.practicum.filmorate.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFound;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.util.Collection;

@Service
public class FilmService {
    private static final Logger log = LoggerFactory.getLogger(FilmService.class);
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    @Autowired
    public FilmService(@Qualifier("testFilmDb") FilmStorage filmStorage, @Qualifier("testUserDb") UserStorage userStorage) {
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

    public Film get(Integer id) throws NotFound {
        try {
            return filmStorage.get(id);
        } catch (DataAccessException e) {
            throw new NotFound("Указан неправильный идентификатор пользователя");
        }
    }

    public Film update(Film film) throws ValidationException, NotFound {
        if (validateFilm(film)) {
            try {
                filmStorage.update(film);
                log.debug("Изменен фильм - {}", film);
            } catch (DataAccessException e) {
                throw new NotFound("Данные о фильме не найдены");
            }
        }
        return film;
    }

    public Film remove(Film film) throws NotFound, ValidationException {
        if (validateFilm(film)) {
            try {
                filmStorage.remove(film);
                log.debug("Удаление фильма - {}", film);
            } catch (DataAccessException e) {
                throw new NotFound("Указан неправильный идентификатор при удалении фильма");
            }
        }
        return film;
    }

    public Collection<Film> getAll() {
        return filmStorage.getAll();
    }

    public boolean addLike(Integer filmId, Integer userId) throws NotFound {
        try {
            User user = userStorage.get(userId);
            return filmStorage.addLike(filmId, user.getId());
        } catch (DataAccessException e) {
            throw new NotFound("При добавлении лайка указаны неправильные идентификаторы");
        }
    }

    public boolean removeLike(Integer filmId, Integer userId) throws NotFound {
        try {
            User user = userStorage.get(userId);
            return filmStorage.removeLike(filmId, user.getId());
        } catch (DataAccessException e) {
            throw new NotFound("При удалении лайка указаны неправильные идентификаторы");
        }
    }

    public Collection<Film> getMostPopularFilms(Integer count) {
        return filmStorage.getMostPopularFilms(count);
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

