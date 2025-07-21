package ru.yandex.practicum.filmorate.storage.dal.extractors;

import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

@Component
public class FilmExtractor implements ResultSetExtractor<Film> {

    @Override
    public Film extractData(ResultSet resultSet) throws SQLException {
        Film film = null;
        while (resultSet.next()) {
            if (film == null) {
                film = FilmMapsUtil.mapFilm(resultSet);
                film.setRating(FilmMapsUtil.mapRating(resultSet));
                film.setGenres(new ArrayList<>());
            }
            Genre genre = FilmMapsUtil.mapGenre(resultSet);
            if (genre != null) {
                film.getGenres().add(genre);
            }
        }
        return film;
    }
}
