package ru.yandex.practicum.filmorate.storage.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFound;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.*;
import java.util.Collection;

@Component
@Qualifier("testUserDb")
public class UserDbStorage implements UserStorage {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public UserDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public User add(User user) throws ValidationException, NotFound {
        String sqlCreateUser = "insert into users(login, name, email, birthday) " +
                "values(?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement preparedStatement = connection.prepareStatement(sqlCreateUser, Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setString(1, user.getLogin());
            preparedStatement.setString(2, user.getName());
            preparedStatement.setString(3, user.getEmail());
            preparedStatement.setDate(4, Date.valueOf(user.getBirthday()));
            return preparedStatement;
        }, keyHolder);
        user.setId(keyHolder.getKey().intValue());
        return user;
    }

    @Override
    public User update(User user) throws NotFound, DataAccessException {
        String sqlUpdateUser = "update users set name=?, login=?, email=?, birthday=? where user_id=?";
        if (jdbcTemplate.update(sqlUpdateUser,
                user.getName(),
                user.getLogin(),
                user.getEmail(),
                user.getBirthday(),
                user.getId()) != 0) {
            return user;
        } else {
            throw new NotFound("При обновлении пользователя указан неправильный идентификатор");
        }
    }

    @Override
    public User remove(User user) throws NotFound, DataAccessException {
        String sqlRemoveUser = "delete from users where user_id=?";
        if (jdbcTemplate.update(sqlRemoveUser, user.getId()) != 0) {
            return null;
        } else {
            throw new NotFound("При удалении пользователя указан неправильный идентификатор");
        }
    }

    @Override
    public User get(Integer id) throws NotFound, DataAccessException {
        String sqlGetUser = "select * from users where user_id=?";
        return jdbcTemplate.queryForObject(sqlGetUser, (rs, rowNum) -> makeUser(rs), id);
    }

    @Override
    public boolean addFriend(Integer id1, Integer id2) throws NotFound, DataAccessException {
        String sqlAddFriend = "insert into friends(user_id, friend_id) " +
                "values(?, ?)";
        if (jdbcTemplate.update(sqlAddFriend, id1, id2) != 0) {
            return true;
        } else {
            throw new NotFound("При добавлении друзей указаны неправильные идентификаторы");
        }
    }

    @Override
    public boolean removeFriend(Integer id1, Integer id2) throws NotFound, DataAccessException {
        String sqlRemoveFriend = "delete from friends where user_id=? and friend_id=?";
        jdbcTemplate.update(sqlRemoveFriend, id1, id2);
        return true;
    }

    @Override
    public Collection<User> getFriends(Integer id) throws NotFound {
        String sqlGetFriends = "select * from users where user_id in (" +
                "select friend_id from friends where user_id=?)";
        return jdbcTemplate.query(sqlGetFriends, (rs, rowNum) -> makeUser(rs), id);
    }

    @Override
    public Collection<User> getCommonFriends(Integer id1, Integer id2) throws NotFound {
        String sqlCommonFriends = "select * from users where user_id in (" +
                "select friend_id from friends where user_id=?) and " +
                "user_id in (select friend_id from friends where user_id=?)";
        return jdbcTemplate.query(sqlCommonFriends, (rs, rowNum) -> makeUser(rs), id1, id2);
    }

    @Override
    public Collection<User> getAll() throws NotFound {
        String sqlGetAll = "select * from users";
        try {
            return jdbcTemplate.query(sqlGetAll, (rs, rowNum) -> makeUser(rs));
        } catch (DataAccessException e) {
            throw new NotFound("Пользователи отсутствуют");
        }
    }

    private User makeUser(ResultSet rs) throws SQLException {
        User result = new User();
        result.setId(rs.getInt("user_id"));
        result.setName(rs.getString("name"));
        result.setLogin(rs.getString("login"));
        result.setEmail(rs.getString("email"));
        result.setBirthday(rs.getDate("birthday").toLocalDate());
        return result;
    }
}
