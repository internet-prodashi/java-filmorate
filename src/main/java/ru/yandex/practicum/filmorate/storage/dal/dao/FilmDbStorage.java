package ru.yandex.practicum.filmorate.storage.dal.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Primary
@Repository
@RequiredArgsConstructor
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;
    private final RowMapper<Film> filmRowMapper;
    private final ResultSetExtractor<Film> filmExtractor;
    private final ResultSetExtractor<List<Film>> filmsExtractor;

    @Override
    public Collection<Film> getFilms() {
        String query = """
                SELECT
                f.film_id AS film_id,
                f.name AS film_name,
                f.description AS description,
                f.release_date AS release_date,
                f.duration AS duration,
                f.rating_id AS rating_id,
                r.name AS rating_name,
                g.genre_id AS genre_id,
                g.name AS genre_name
                FROM films f
                JOIN ratings r ON f.rating_id = r.rating_id
                LEFT JOIN film_genres fg ON f.film_id = fg.film_id
                LEFT JOIN genres g ON fg.genre_id = g.genre_id
                ORDER BY f.film_id
                """;
        return jdbcTemplate.query(query, filmsExtractor);
    }

    @Override
    public Film createFilm(Film film) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        String query = "INSERT INTO films (name, description, release_date, duration, rating_id) VALUES (?, ?, ?, ?, ?)";
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, film.getName());
            ps.setString(2, film.getDescription());
            ps.setDate(3, java.sql.Date.valueOf(film.getReleaseDate()));
            ps.setLong(4, film.getDuration().toMinutes());
            ps.setLong(5, film.getRating().getId());
            return ps;
        }, keyHolder);
        Long id = keyHolder.getKeyAs(Long.class);
        if (id == null) {
            throw new IllegalStateException("Не удалось сгенерировать идентификатор фильма");
        }
        film.setId(id);
        return film;
    }

    @Override
    public Optional<Film> updateFilm(Film film) {
        String query = """
                UPDATE films
                SET name = ?, description = ?, release_date = ?, duration = ?, rating_id = ?
                WHERE film_id = ?;
                """;
        int countRows = jdbcTemplate.update(query,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getRating().getId(),
                film.getId()
        );
        if (countRows > 0) {
            return Optional.of(film);
        }
        return Optional.empty();
    }

    @Override
    public Optional<Film> getFilmById(Long id) {
        String query = """
                SELECT
                f.film_id AS film_id,
                f.name AS film_name,
                f.description AS description,
                f.release_date AS release_date,
                f.duration AS duration,
                f.rating_id AS rating_id,
                r.name AS rating_name,
                g.genre_id AS genre_id,
                g.name AS genre_name
                FROM films f
                JOIN ratings r ON f.rating_id = r.rating_id
                LEFT JOIN film_genres fg ON f.film_id = fg.film_id
                LEFT JOIN genres g ON fg.genre_id = g.genre_id
                WHERE f.film_id = ?
                """;
        return Optional.ofNullable(jdbcTemplate.query(query, filmExtractor, id));
    }

    @Override
    public void addLike(Long filmId, Long userId) {
        String query = "INSERT INTO film_likes(user_id, film_id) VALUES (?, ?)";
        jdbcTemplate.update(query, userId, filmId);
    }

    @Override
    public void removeLike(Long filmId, Long userId) {
        String query = "DELETE FROM film_likes WHERE user_id = ? AND film_id = ?";
        jdbcTemplate.update(query, userId, filmId);
    }

    @Override
    public List<Film> getPopularFilms(int count) {
        String query = """
                SELECT f.film_id, f.name, f.description, f.release_date, f.duration, f.rating_id
                FROM films f
                LEFT JOIN (
                SELECT film_id, COUNT(user_id) AS count_like
                FROM film_likes
                GROUP BY film_id
                ) fl ON f.film_id = fl.film_id
                ORDER BY fl.count_like DESC
                LIMIT ?
                """;
        return jdbcTemplate.query(query, filmRowMapper, count);
    }

}
