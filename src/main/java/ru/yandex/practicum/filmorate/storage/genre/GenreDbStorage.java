package ru.yandex.practicum.filmorate.storage.genre;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFound;
import ru.yandex.practicum.filmorate.model.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;

@Component
public class GenreDbStorage {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    GenreDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Genre getGenreById(Integer id) throws NotFound {
        String queryGenreById = "select * from genre where genre_id=?";
        try {
            return jdbcTemplate.queryForObject(queryGenreById, (rs, rowNum) -> makeGenre(rs), id);
        } catch (DataAccessException e) {
            throw new NotFound("Укакзан неправильный идентификатор жанра");
        }
    }

    public Collection<Genre> getAllGenre() {
        String queryGenres = "select * from genre";
        return jdbcTemplate.query(queryGenres, (rs, rowNum) -> makeGenre(rs));
    }

    private Genre makeGenre(ResultSet rs) throws SQLException {
        Integer id = Integer.parseInt(rs.getString("genre_id"));
        String name = rs.getString("name");
        Genre genre = new Genre();
        genre.setId(id);
        genre.setName(name);
        return genre;
    }
}
