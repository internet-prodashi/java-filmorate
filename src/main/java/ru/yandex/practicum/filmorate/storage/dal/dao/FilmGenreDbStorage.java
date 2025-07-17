package ru.yandex.practicum.filmorate.storage.dal.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class FilmGenreDbStorage {
    private final JdbcTemplate jdbcTemplate;

    public void saveFilmGenres(Long filmId, List<Genre> genres) {
        String query = """
                INSERT INTO film_genres(film_id, genre_id)
                VALUES (?, ?)
                """;
        List<Object[]> params = genres
                .stream()
                .map(Genre::getId)
                .distinct()
                .map(genreId -> new Object[]{filmId, genreId})
                .toList();
        jdbcTemplate.batchUpdate(query, params);
    }

    public void deleteFilmGenresForFilmId(Long filmId) {
        String query = "DELETE FROM film_genres WHERE film_id = ?";
        jdbcTemplate.update(query, filmId);
    }
}
