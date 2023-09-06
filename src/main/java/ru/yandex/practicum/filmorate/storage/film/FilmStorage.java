package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.exception.NotFound;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;

public interface FilmStorage {
    Film add(Film film) throws ValidationException;
    Film update(Film film) throws NotFound;
    Film remove(Film film) throws NotFound;
    Collection<Film> getAll();
    Film getFilm(Integer id) throws NotFound;
}
