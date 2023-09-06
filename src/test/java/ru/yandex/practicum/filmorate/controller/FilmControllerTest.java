package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.film.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;

class FilmControllerTest {
    private FilmController filmController;

    @BeforeEach
    void createFilmController() {
        UserStorage userStorage = new InMemoryUserStorage();
        FilmStorage filmStorage = new InMemoryFilmStorage();
        FilmService filmService = new FilmService(filmStorage, userStorage);
        filmController = new FilmController(filmService);
    }

    @Test
    void createFilmWithoutName() {
        Film film = new Film();
        film.setId(1);
        film.setName("");
        film.setDescription("test");
        film.setReleaseDate(LocalDate.of(2010, 10, 20));
        film.setDuration(100);

        ValidationException exception = Assertions.assertThrows(ValidationException.class, () -> {
            filmController.createFilm(film);
        });

        Assertions.assertEquals("Название фильма не может быть пустым", exception.getMessage());
        Assertions.assertEquals(0, filmController.getFilms().size());
    }

    @Test
    void createFilmWithDescription200Symbols() {
        Film film = new Film();
        film.setId(1);
        film.setName("test");
        String description = new String(new char[201]).replace("\0", "t");
        film.setDescription(description);
        film.setReleaseDate(LocalDate.of(2010, 10, 20));
        film.setDuration(100);

        ValidationException exception = Assertions.assertThrows(ValidationException.class, () -> {
            filmController.createFilm(film);
        });

        Assertions.assertEquals("Максимальная длина описания - 200 символов", exception.getMessage());
        Assertions.assertEquals(0, filmController.getFilms().size());
    }

    @Test
    void createFilmWithDateReleaseBeforeBirthday() {
        Film film = new Film();
        film.setId(1);
        film.setName("test");
        film.setDescription("test");
        film.setReleaseDate(LocalDate.of(1895, 12, 27));
        film.setDuration(100);

        ValidationException exception = Assertions.assertThrows(ValidationException.class, () -> {
            filmController.createFilm(film);
        });

        Assertions.assertEquals("Дата релиза - не раньше 28 декабря 1895 года", exception.getMessage());
        Assertions.assertEquals(0, filmController.getFilms().size());
    }

    @Test
    void createFilmWithDuration0() {
        Film film = new Film();
        film.setId(1);
        film.setName("test");
        film.setDescription("test");
        film.setReleaseDate(LocalDate.of(2010, 10, 20));
        film.setDuration(0);

        ValidationException exception = Assertions.assertThrows(ValidationException.class, () -> {
            filmController.createFilm(film);
        });

        Assertions.assertEquals("Продолжительность фильма должна быть положительной", exception.getMessage());
        Assertions.assertEquals(0, filmController.getFilms().size());
    }
}