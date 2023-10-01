package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFound;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.Date;
import java.sql.*;
import java.util.*;

@Component
@Qualifier("testFilmDb")
public class FilmDbStorage implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;

    public FilmDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Film add(Film film) throws ValidationException {
        String sqlCreateUser = "insert into films(name, description, release_date, duration, rating_id) " +
                "values(?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement preparedStatement = connection.prepareStatement(sqlCreateUser, Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setString(1, film.getName());
            preparedStatement.setString(2, film.getDescription());
            preparedStatement.setDate(3, Date.valueOf(film.getReleaseDate()));
            preparedStatement.setLong(4, film.getDuration());
            preparedStatement.setInt(5, film.getMpa().getId());
            return preparedStatement;
        }, keyHolder);
        film.setId(keyHolder.getKey().intValue());
        updateFilmGenre(film);
        return film;
    }

    @Override
    public Film update(Film film) throws NotFound {
        String sqlUpdateFilm = "update films set name=?, description=?, release_date=?, duration=?, rating_id=? where film_id=?";
        try {
            int responseCode = jdbcTemplate.update(sqlUpdateFilm,
                    film.getName(),
                    film.getDescription(),
                    film.getReleaseDate(),
                    film.getDuration(),
                    film.getMpa().getId(),
                    film.getId());
            if (responseCode != 0) {
                updateFilmGenre(film);
                Set<Genre> swapGenres = new TreeSet<>(Comparator.comparingInt(Genre::getId));
                swapGenres.addAll(film.getGenres());
                film.setGenres(swapGenres);
                return film;
            } else {
                throw new NotFound("Не правильные данные о фильме");
            }
        } catch (DataAccessException e) {
            throw new NotFound("Не правильные данные о фильме");
        }
    }

    @Override
    public Film getFilm(Integer id) throws NotFound {
        String sqlGetFilm = "select * from films where film_id=?";
        try {
            Film result = jdbcTemplate.queryForObject(sqlGetFilm, (rs, rowNum) -> makeFilm(rs), id);
            result.setGenres(getFilmGenre(result.getId()));
            result.getMpa().setName(jdbcTemplate.queryForObject("select name from rating where rating_id=?",
                    String.class, result.getMpa().getId()));
            return result;
        } catch (DataAccessException e) {
            throw new NotFound("Указан неправильный идентификатор фильма");
        }
    }

    @Override
    public Collection<Film> getAll() {
        String sqlGetAllFilms = "select * from films";
        List<Film> result = jdbcTemplate.query(sqlGetAllFilms, (rs, rowNum) -> makeFilm(rs));
        for (Film film : result) {
            film.setGenres(getFilmGenre(film.getId()));
            film.getMpa().setName(jdbcTemplate.queryForObject("select name from rating where rating_id=?", String.class, film.getMpa().getId()));
        }
        return result;
    }

    @Override
    public Film remove(Film film) throws NotFound {
        String sqlForRemoveFilm = "delete from films where film_id=?";
        try {
            jdbcTemplate.update(sqlForRemoveFilm, film.getId());
            return null;
        } catch (DataAccessException e) {
            throw new NotFound("Указан неправильный идентификатор фильма при удалении");
        }
    }

    @Override
    public boolean addLike(Integer id, Integer userId) throws NotFound {
        String sqlLikeAdd = "insert into film_likes(film_id, user_id) values(?, ?)";
        try {
            jdbcTemplate.update(sqlLikeAdd, id, userId);
            return true;
        } catch (DataAccessException e) {
            throw new NotFound("При добавлении лайка указаны неправильные идентификаторы");
        }
    }

    @Override
    public boolean removeLike(Integer id, Integer userId) throws NotFound {
        String sqlRemoveLike = "delete from film_likes where film_id=? and user_id=?";
        try {
            int responseCode = jdbcTemplate.update(sqlRemoveLike, id, userId);
            if (responseCode != 0) {
                return true;
            } else {
                throw new NotFound("При удалении лайка указаны неправильные идентификаторы");
            }
        } catch (DataAccessException e) {
            throw new NotFound("При удалении лайка указаны неправильные идентификаторы");
        }
    }

    @Override
    public Collection<Film> getMostPopularFilms(Integer count) {
        String sqlMostPopularFilm = "select * from films as f left join film_likes as fl on f.film_id=fl.film_id " +
                "group by f.film_id order by count(fl.user_id) desc limit ?";
        List<Film> result = jdbcTemplate.query(sqlMostPopularFilm, (rs, rowNum) -> makeFilm(rs), count);
        for (Film film : result) {
            film.setGenres(getFilmGenre(film.getId()));
        }
        return result;
    }

    private Film makeFilm(ResultSet rs) throws SQLException {
        Film result = new Film();
        result.setId(rs.getInt("film_id"));
        result.setName(rs.getString("name"));
        result.setDescription(rs.getString("description"));
        result.setReleaseDate(rs.getDate("release_date").toLocalDate());
        result.setDuration(rs.getInt("duration"));
        Mpa mpa = new Mpa();
        mpa.setId(rs.getInt("rating_id"));
        mpa.setName(jdbcTemplate.queryForObject("select name from rating where rating_id=?", String.class, mpa.getId()));
        result.setMpa(mpa);
        return result;
    }

    private boolean updateFilmGenre(Film film) {
        jdbcTemplate.update("delete from film_genre where film_id=?", film.getId());
        String sqlAddFilmGenre = "insert into film_genre(film_id, genre_id) values(?, ?)";
        for (Genre genre : film.getGenres()) {
            jdbcTemplate.update(sqlAddFilmGenre, film.getId(), genre.getId());
        }
        return true;
    }

    private Set<Genre> getFilmGenre(Integer filmId) {
        String sqlGetFilmGenre = "select * from genre where genre_id in (" +
                "select genre_id from film_genre where film_id=?)";
        return new HashSet<>(jdbcTemplate.query(sqlGetFilmGenre, (rs, rowNum) -> {
            Genre genre = new Genre();
            genre.setId(rs.getInt("genre_id"));
            genre.setName(rs.getString("name"));
            return genre;
        }, filmId));
    }
}
