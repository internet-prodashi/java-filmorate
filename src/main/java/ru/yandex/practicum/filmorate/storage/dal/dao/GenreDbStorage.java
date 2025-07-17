package ru.yandex.practicum.filmorate.storage.dal.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class GenreDbStorage {
    private final JdbcTemplate jdbcTemplate;
    private final RowMapper<Genre> genreRowMapper;

    public Optional<Genre> getGenreById(Long id) {
        String query = """
                SELECT genre_id, name
                FROM genres
                WHERE genre_id = ?
                """;
        List<Genre> genres = jdbcTemplate.query(query, genreRowMapper, id);
        if (genres.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(genres.getFirst());
    }

    public Collection<Genre> getGenres() {
        String query = """
                SELECT genre_id, name
                FROM genres
                ORDER BY genre_id
                """;
        return jdbcTemplate.query(query, genreRowMapper);
    }
}
