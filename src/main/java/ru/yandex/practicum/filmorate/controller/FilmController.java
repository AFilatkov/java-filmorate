package ru.yandex.practicum.filmorate.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;

@RestController
@RequestMapping("/films")
public class FilmController {
    private static final Logger log = LoggerFactory.getLogger(FilmController.class);
    private HashMap<Integer, Film> films = new HashMap<>();
    private int currentId = 1;

    @GetMapping
    public Collection<Film> getFilms() {
        return films.values();
    }

    @PostMapping
    public Film createFilm(@RequestBody Film film) throws ValidationException {
        if (validateFilm(film)) {
            log.debug("Добавлен фильм - {}", film);
            films.put(currentId, film);
            film.setId(currentId);
            currentId += 1;
        }
        return film;
    }

    @PutMapping
    public Film updateFilm(@RequestBody Film film) throws ValidationException {
        if (validateFilm(film)) {
            log.debug("Изменен фильм - {}", film);
            if (films.containsKey(film.getId())) {
                films.put(film.getId(), film);
                return film;
            } else {
                throw new ValidationException("Заданный идентификатор фильма не найден");
            }
        } else {
            return film;
        }
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
