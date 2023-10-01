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
        try {
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
        } catch (DataAccessException e) {
            throw new NotFound("Данные о пользователе в базе не найдены");
        }
    }

    @Override
    public User update(User user) throws NotFound {
        String sqlUpdateUser = "update users set name=?, login=?, email=?, birthday=? where user_id=?";
        try {
            int responseCode = jdbcTemplate.update(sqlUpdateUser,
                    user.getName(),
                    user.getLogin(),
                    user.getEmail(),
                    user.getBirthday(),
                    user.getId());
            if (responseCode != 0)
                return user;
            else
                throw new NotFound("Данные о пользователе в базе не найдены");
        } catch (DataAccessException e) {
            throw new NotFound("Данные о пользователе в базе не найдены");
        }
    }

    @Override
    public User remove(User user) throws NotFound {
        String sqlRemoveUser = "delete from users where user_id=?";
        try {
            jdbcTemplate.update(sqlRemoveUser, user.getId());
            return null;
        } catch (DataAccessException e) {
            throw new NotFound("Пользователь при удалении не найден");
        }
    }

    @Override
    public User getUser(Integer id) throws NotFound {
        String sqlGetUser = "select * from users where user_id=?";
        try {
            return jdbcTemplate.queryForObject(sqlGetUser, (rs, rowNum) -> makeUser(rs), id);
        } catch (DataAccessException e) {
            throw new NotFound("Указан неправильный идентификатор");
        }
    }

    @Override
    public boolean addFriend(Integer id1, Integer id2) throws NotFound {
        String sqlAddFriend = "insert into friends(user_id, friend_id) " +
                "values(?, ?)";
        try {
            jdbcTemplate.update(sqlAddFriend, id1, id2);
            return true;
        } catch (DataAccessException e) {
            throw new NotFound("Указан неправильный идентификатор");
        }
    }

    @Override
    public boolean removeFriend(Integer id1, Integer id2) throws NotFound {
        String sqlRemoveFriend = "delete from friends where user_id=? and friend_id=?";
        try {
            jdbcTemplate.update(sqlRemoveFriend, id1, id2);
            return true;
        } catch (DataAccessException e) {
            throw new NotFound("Указан неправильный идентификатор при удалении друзей");
        }
    }

    @Override
    public Collection<User> getFriends(Integer id) throws NotFound {
        String sqlGetFriends = "select * from users where user_id in (" +
                "select friend_id from friends where user_id=?)";
        try {
            return jdbcTemplate.query(sqlGetFriends, (rs, rowNum) -> makeUser(rs), id);
        } catch (DataAccessException e) {
            throw new NotFound("Указан неправильный идентификатор при получении друзей");
        }
    }

    @Override
    public Collection<User> getCommonFriends(Integer id1, Integer id2) throws NotFound {
        String sqlCommonFriends = "select * from users where user_id in (" +
                "select friend_id from friends where user_id=?) and " +
                "user_id in (select friend_id from friends where user_id=?)";
       try {
           return jdbcTemplate.query(sqlCommonFriends, (rs, rowNum) -> makeUser(rs), id1, id2);
       } catch (DataAccessException e) {
           throw new NotFound("Указан неправильный идентификатор при получении списка общих друзей");
       }
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
