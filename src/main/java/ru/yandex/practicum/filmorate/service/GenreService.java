package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFound;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.genre.GenreDbStorage;

import java.util.Collection;

@Service
public class GenreService {
    private final GenreDbStorage genreDbStorage;

    @Autowired
    GenreService(GenreDbStorage genreDbStorage) {
        this.genreDbStorage = genreDbStorage;
    }

    public Genre getGenreById(Integer id) throws NotFound {
        return genreDbStorage.getGenreById(id);
    }

    public Collection<Genre> getAllGenre() {
        return genreDbStorage.getAllGenre();
    }
}
