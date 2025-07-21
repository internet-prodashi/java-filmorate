package ru.yandex.practicum.filmorate.storage.dal.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Rating;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class RatingDbStorage {
    private final JdbcTemplate jdbcTemplate;
    private final RowMapper<Rating> ratingRowMapper;

    public Optional<Rating> getRatingById(Long id) {
        String query = """
                SELECT rating_id, name
                FROM ratings
                WHERE rating_id = ?
                """;
        List<Rating> ratings = jdbcTemplate.query(query, ratingRowMapper, id);
        if (ratings.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(ratings.getFirst());
    }

    public Collection<Rating> getRatings() {
        String query = """
                SELECT rating_id, name
                FROM ratings
                ORDER BY rating_id
                """;
        return jdbcTemplate.query(query, ratingRowMapper);
    }
}
