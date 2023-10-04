package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.exception.NotFound;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.genre.GenreDbStorage;
import ru.yandex.practicum.filmorate.storage.mpa.MpaDbStorage;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;

import java.time.LocalDate;
import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class FilmorateApplicationTests {
    private final UserDbStorage userStorage;
    private final GenreDbStorage genreStorage;
    private final MpaDbStorage mpaStorage;
    private final FilmDbStorage filmStorage;

    @Test
    public void testFindMpaById() throws NotFound {
        Mpa mpa = mpaStorage.getMpaById(1);
        assertThat(mpa).isNotNull()
                .hasFieldOrPropertyWithValue("id", 1)
                .hasFieldOrPropertyWithValue("name", "G");
    }

    @Test
    public void testCheckAllMpa() {
        Collection<Mpa> mpas = mpaStorage.getAllMpa();
        assertThat(mpas).hasSize(5);
    }

    @Test
    public void testFindGenreById() throws NotFound {
        Genre genre = genreStorage.getGenreById(1);
        assertThat(genre).isNotNull()
                .hasFieldOrPropertyWithValue("id", 1);
    }

    @Test
    public void testCheckAllGenres() {
        Collection<Genre> genres = genreStorage.getAllGenre();
        assertThat(genres).hasSize(6);
    }

    @Test
    public void testCreateAndUpdateUserById() throws ValidationException, NotFound {
        User user = new User();
        user.setName("testName");
        user.setLogin("testLogin");
        user.setEmail("testEmail");
        user.setBirthday(LocalDate.of(1987, 10, 15));
        user = userStorage.add(user);
        assertThat(user).isNotNull()
                .hasFieldOrPropertyWithValue("id", 1);

        user.setName("updateName");
        user = userStorage.update(user);
        assertThat(user).isNotNull()
                .hasFieldOrPropertyWithValue("id", 1)
                .hasFieldOrPropertyWithValue("name", "updateName");

        user = userStorage.remove(user);
        assertThat(user).isNull();
    }

    @Test
    public void testAddFriends() throws ValidationException, NotFound {
        User user = new User();
        user.setName("testName");
        user.setLogin("testLogin");
        user.setEmail("testEmail");
        user.setBirthday(LocalDate.of(1987, 10, 15));
        user = userStorage.add(user);

        User friend = new User();
        friend.setName("friend");
        friend.setLogin("friend");
        friend.setEmail("friend");
        friend.setBirthday(LocalDate.of(2011, 11, 20));
        friend = userStorage.add(friend);

        User common = new User();
        common.setName("common");
        common.setLogin("common");
        common.setEmail("common");
        common.setBirthday(LocalDate.of(1999, 9, 9));
        common = userStorage.add(common);

        userStorage.addFriend(user.getId(), common.getId());
        Collection<User> testFriends = userStorage.getFriends(user.getId());
        assertThat(testFriends).isNotNull().hasSize(1).contains(common);

        userStorage.addFriend(friend.getId(), common.getId());
        testFriends = userStorage.getCommonFriends(user.getId(), friend.getId());
        assertThat(testFriends).hasSize(1).contains(common);

        userStorage.removeFriend(user.getId(), common.getId());
        testFriends = userStorage.getFriends(user.getId());
        assertThat(testFriends).hasSize(0);
    }

    @Test
    public void testAddFilms() throws ValidationException, NotFound {
        Film film = new Film();
        film.setName("film");
        film.setDescription("film");
        film.setReleaseDate(LocalDate.of(1999, 2, 20));
        film.setDuration(100);
        Mpa mpa = new Mpa();
        mpa.setId(1);
        film.setMpa(mpa);

        film = filmStorage.add(film);
        assertThat(film).isNotNull()
                .hasFieldOrPropertyWithValue("id", 1)
                .hasFieldOrPropertyWithValue("name", "film");

        film.setDescription("update");
        film = filmStorage.update(film);
        assertThat(film).isNotNull()
                .hasFieldOrPropertyWithValue("description", "update");

        Collection<Film> films = filmStorage.getAll();
        assertThat(films).hasSize(1);

        Film testGetFilmById = filmStorage.get(1);
        assertThat(testGetFilmById).isNotNull()
                .hasFieldOrPropertyWithValue("id", 1);

        User user = new User();
        user.setName("testName");
        user.setLogin("testLogin");
        user.setEmail("testEmail");
        user.setBirthday(LocalDate.of(1987, 10, 15));
        user = userStorage.add(user);

        filmStorage.addLike(film.getId(), user.getId());

        Film filmWithoutRate = new Film();
        filmWithoutRate.setName("test");
        filmWithoutRate.setDescription("test");
        filmWithoutRate.setReleaseDate(LocalDate.of(1990, 1, 10));
        filmWithoutRate.setDuration(150);
        Mpa mpaWithoutRate = new Mpa();
        mpaWithoutRate.setId(2);
        filmWithoutRate.setMpa(mpaWithoutRate);
        filmWithoutRate = filmStorage.add(filmWithoutRate);

        filmStorage.addLike(filmWithoutRate.getId(), user.getId());
        filmStorage.removeLike(filmWithoutRate.getId(), user.getId());

        Collection<Film> filmRate = filmStorage.getMostPopularFilms(1);
        assertThat(filmRate).hasSize(1);
    }
}
