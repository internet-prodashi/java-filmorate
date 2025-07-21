package ru.yandex.practicum.filmorate.storage.dal.extractors;

import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Component
public class FilmsExtractor implements ResultSetExtractor<List<Film>> {

    @Override
    public List<Film> extractData(ResultSet resultSet) throws SQLException {
        Map<Long, Film> films = new HashMap<>();
        while (resultSet.next()) {
            long filmId = resultSet.getLong("film_id");
            Film film = films.get(filmId);
            if (film == null) {
                film = FilmMapsUtil.mapFilm(resultSet);
                film.setRating(FilmMapsUtil.mapRating(resultSet));
                film.setGenres(new ArrayList<>());
                films.put(filmId, film);
            }
            Genre genre = FilmMapsUtil.mapGenre(resultSet);
            if (genre != null) {
                film.getGenres().add(genre);
            }
        }
        return new ArrayList<>(films.values());
    }
}
