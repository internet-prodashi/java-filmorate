package ru.yandex.practicum.filmorate.dao;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.dal.dao.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.dal.dao.UserDbStorage;

import java.time.Duration;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@ComponentScan(basePackages = {"ru.yandex.practicum.filmorate.storage"})
class FilmDbStorageTest {
    private final FilmDbStorage filmDbStorage;
    private final UserDbStorage userDbStorage;
    private final JdbcTemplate jdbcTemplate;

    private Film createFilm(String name, String description) {
        Film film = new Film();
        film.setName(name);
        film.setDescription(description);
        film.setReleaseDate(LocalDate.of(1990, 2, 5));
        film.setDuration(Duration.ofMinutes(120));
        film.setRating(new Rating(5L, "NC-17"));
        film.setGenres(List.of());
        return film;
    }

    private User createUser(String login, String name) {
        User user = new User();
        user.setEmail("email@mail.ru");
        user.setLogin(login);
        user.setName(name);
        user.setBirthday(LocalDate.of(1990, 2, 5));
        return user;
    }

    @Test
    void createFilm() {
        Film film = createFilm("Название фильма №1", "Описание фильма №1");
        Film savedFilm = filmDbStorage.createFilm(film);
        Optional<Film> loadedFilm = filmDbStorage.getFilmById(savedFilm.getId());

        assertThat(savedFilm.getId()).isNotNull();
        assertThat(loadedFilm).isPresent();
        assertThat(loadedFilm.get().getName()).isEqualTo("Название фильма №1");
        assertThat(loadedFilm.get().getDescription()).isEqualTo("Описание фильма №1");
    }

    @Test
    void findFilm() {
        Film film2 = createFilm("Название фильма №2", "Описание фильма №2");
        filmDbStorage.createFilm(film2);
        Optional<Film> findFilmYes = filmDbStorage.getFilmById(film2.getId());
        assertThat(findFilmYes).isPresent();
        Optional<Film> findFilmNot = filmDbStorage.getFilmById(999L);
        assertThat(findFilmNot).isEmpty();

        Film film3 = createFilm("Название фильма №3", "Описание фильма №3");
        Film film4 = createFilm("Название фильма №4", "Описание фильма №4");
        filmDbStorage.createFilm(film3);
        filmDbStorage.createFilm(film4);

        Collection<Film> films = filmDbStorage.getFilms();
        assertThat(films).hasSize(3);
    }

    @Test
    void updateFilm() {
        Film film5 = createFilm("Название фильма №5", "Описание фильма №5");
        filmDbStorage.createFilm(film5);
        film5.setName("Название фильма №5 новое");
        film5.setDescription("Описание фильма №5 новое");
        Optional<Film> updatedFilm = filmDbStorage.updateFilm(film5);

        assertThat(updatedFilm).isPresent();
        assertThat(updatedFilm.get().getName()).isEqualTo("Название фильма №5 новое");
        assertThat(updatedFilm.get().getDescription()).isEqualTo("Описание фильма №5 новое");

        Optional<Film> loadedFilm = filmDbStorage.getFilmById(film5.getId());
        assertThat(loadedFilm).isPresent();
        assertThat(loadedFilm.get().getName()).isEqualTo(updatedFilm.get().getName());
        assertThat(loadedFilm.get().getDescription()).isEqualTo(updatedFilm.get().getDescription());

        Film film6 = createFilm("Название фильма №6", "Описание фильма №6");
        film6.setId(7L);

        Optional<Film> updateFilm = filmDbStorage.updateFilm(film6);
        assertThat(updateFilm).isEmpty();
    }

    @Test
    void addAndDeleteLike() {
        Film film7 = createFilm("Название фильма №7", "Описание фильма №7");
        filmDbStorage.createFilm(film7);
        Film film8 = createFilm("Название фильма №8", "Описание фильма №8");
        filmDbStorage.createFilm(film8);
        User user1 = createUser("Логин №1", "Имя №1");
        userDbStorage.createUser(user1);
        User user2 = createUser("Логин №2", "Имя №2");
        userDbStorage.createUser(user2);
        filmDbStorage.addLike(film7.getId(), user1.getId());
        filmDbStorage.addLike(film8.getId(), user1.getId());
        filmDbStorage.addLike(film8.getId(), user2.getId());

        int countOneLike = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM film_likes WHERE user_id = ? AND film_id = ?",
                Integer.class, user1.getId(), film7.getId()
        );
        assertThat(countOneLike).isEqualTo(1);
        int countAllLikeOneUser = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM film_likes WHERE user_id = ?",
                Integer.class, user1.getId()
        );
        assertThat(countAllLikeOneUser).isEqualTo(2);
        int countAllLikeAllUser = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM film_likes", Integer.class);
        assertThat(countAllLikeAllUser).isEqualTo(3);

        filmDbStorage.removeLike(film7.getId(), user1.getId());

        Integer countDeleteLike = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM film_likes WHERE user_id = ? AND film_id = ?",
                Integer.class, film7.getId(), user1.getId()
        );
        assertThat(countDeleteLike).isZero();
    }

    @Test
    void findTopFilmsByLikes() {
        User user3 = createUser("Логин №3", "Имя №3");
        userDbStorage.createUser(user3);
        User user4 = createUser("Логин №4", "Имя №4");
        userDbStorage.createUser(user4);
        Film film9 = createFilm("Название фильма №9", "Описание фильма №9");
        filmDbStorage.createFilm(film9);
        Film film10 = createFilm("Название фильма №10", "Описание фильма №10");
        filmDbStorage.createFilm(film10);
        jdbcTemplate.update("INSERT INTO film_likes (user_id, film_id) VALUES (?, ?)", user3.getId(), film9.getId());
        jdbcTemplate.update("INSERT INTO film_likes (user_id, film_id) VALUES (?, ?)", user4.getId(), film9.getId());
        jdbcTemplate.update("INSERT INTO film_likes (user_id, film_id) VALUES (?, ?)", user3.getId(), film10.getId());

        List<Film> resultListPopularFilms = filmDbStorage.getPopularFilms(10);

        assertThat(resultListPopularFilms).hasSize(2);
        assertThat(resultListPopularFilms.get(0).getId()).isEqualTo(1L);
        assertThat(resultListPopularFilms.get(1).getId()).isEqualTo(2L);
    }
}