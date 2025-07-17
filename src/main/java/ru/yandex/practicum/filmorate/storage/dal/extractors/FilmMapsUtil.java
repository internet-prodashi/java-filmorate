package ru.yandex.practicum.filmorate.storage.dal.extractors;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Rating;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Duration;

public class FilmMapsUtil {
    public static Film mapFilm(ResultSet resultSet) throws SQLException {
        Film film = new Film();
        film.setId(resultSet.getLong("film_id"));
        film.setName(resultSet.getString("film_name"));
        film.setDescription(resultSet.getString("description"));
        film.setReleaseDate(resultSet.getDate("release_date").toLocalDate());
        film.setDuration(Duration.ofMinutes(resultSet.getInt("duration")));
        return film;
    }

    public static Rating mapRating(ResultSet resultSet) throws SQLException {
        Rating rating = new Rating();
        rating.setId(resultSet.getLong("rating_id"));
        rating.setName(resultSet.getString("rating_name"));
        return rating;
    }

    public static Genre mapGenre(ResultSet resultSet) throws SQLException {
        long genreId = resultSet.getLong("genre_id");
        if (genreId <= 0) {
            return null;
        }
        Genre genre = new Genre();
        genre.setId(genreId);
        genre.setName(resultSet.getString("genre_name"));
        return genre;
    }
}
