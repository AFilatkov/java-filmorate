package ru.yandex.practicum.filmorate.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NotFound;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.Collection;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/films")
public class FilmController {
    private static final Logger log = LoggerFactory.getLogger(FilmController.class);
    private FilmService filmService;

    @Autowired
    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    @GetMapping
    public Collection<Film> getFilms() {
        return filmService.getAll();
    }

    @PostMapping
    public Film createFilm(@RequestBody Film film) throws ValidationException {
        filmService.add(film);
        return film;
    }

    @PutMapping
    public Film updateFilm(@RequestBody Film film) throws NotFound, ValidationException {
        filmService.update(film);
        return film;
    }

    @GetMapping("/{id}")
    public Film gitFilm(@PathVariable Integer id) throws NotFound {
        return filmService.getFilm(id);
    }

    @PutMapping("/{id}/like/{userId}")
    public Film addLike(@PathVariable Integer id, @PathVariable Integer userId) throws NotFound {
        return filmService.addLike(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public Film removeLike(@PathVariable Integer id, @PathVariable Integer userId) throws NotFound {
        return filmService.removeLike(id, userId);
    }

    @GetMapping("/popular")
    public List<Film> getPopular(@RequestParam(name = "count", defaultValue = "10") String count) {
        return filmService.getMostPopularFilms(Integer.parseInt(count));
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> validationError(final ValidationException e) {
        return Map.of("errorMessage", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> notFoundError(final NotFound e) {
        return Map.of("errorMessage", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Map<String, String> internalError(RuntimeException e) {
        return Map.of("errorMessage", e.getMessage());
    }
}
