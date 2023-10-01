package ru.yandex.practicum.filmorate.storage.mpa;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFound;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;

@Component
public class MpaDbStorage {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    MpaDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Mpa getMpaById(Integer id) throws NotFound {
        String queryMpaById = "select * from rating where rating_id=?";
        try {
            return jdbcTemplate.queryForObject(queryMpaById, (rs, rowNum) -> makeMpa(rs), id);
        } catch (DataAccessException e) {
            throw new NotFound("Указан неправильный идентификатор возрастного рейтинга");
        }
    }

    public Collection<Mpa> getAllMpa() {
        String queryMpa = "select * from rating";
        return jdbcTemplate.query(queryMpa, (rs, rowNum) -> makeMpa(rs));
    }

    private Mpa makeMpa(ResultSet rs) throws SQLException {
        Integer id = Integer.parseInt(rs.getString("rating_id"));
        String name = rs.getString("name");
        Mpa mpa = new Mpa();
        mpa.setId(id);
        mpa.setName(name);
        return mpa;
    }
}
