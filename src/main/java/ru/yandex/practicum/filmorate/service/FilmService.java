package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.filmorate.exception.ExceptionNotFound;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.storage.dal.dao.FilmGenreDbStorage;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class FilmService {

    private final FilmStorage filmStorage;
    private final UserService userService;
    private final GenreService genreService;
    private final RatingService ratingService;
    private final FilmGenreDbStorage filmGenreDbStorage;

    public Collection<Film> getFilms() {
        return filmStorage.getFilms();
    }

    @Transactional
    public Film createFilm(Film film) {
        validateFilm(film);
        Film createdFilm = filmStorage.createFilm(film);
        if (film.getGenres() != null && !film.getGenres().isEmpty()) {
            filmGenreDbStorage.saveFilmGenres(createdFilm.getId(), film.getGenres());
        }
        return createdFilm;
    }

    @Transactional
    public Film updateFilm(Film film) {
        validateFilm(film);
        Film updatedFilm = filmStorage.updateFilm(film)
                .orElseThrow(() -> new ExceptionNotFound("Фильм с идентификатором '%d' не найден".formatted(film.getId())));
        filmGenreDbStorage.deleteFilmGenresForFilmId(updatedFilm.getId());
        if (film.getGenres() != null && !film.getGenres().isEmpty()) {
            filmGenreDbStorage.saveFilmGenres(updatedFilm.getId(), film.getGenres());
        }
        return updatedFilm;
    }

    public void addLike(Long filmId, Long userId) {
        if (userService.getUserById(userId).getId() > 0 && getFilmById(filmId).getId() > 0) {
            filmStorage.addLike(filmId, userId);
        }
    }

    public void removeLike(Long filmId, Long userId) {
        if (userService.getUserById(userId).getId() > 0 && getFilmById(filmId).getId() > 0) {
            filmStorage.removeLike(filmId, userId);
        }
    }

    public List<Film> getPopularFilms(int count) {
        return filmStorage.getPopularFilms(count);
    }

    public Film getFilmById(Long id) {
        return filmStorage.getFilmById(id)
                .orElseThrow(() -> new ExceptionNotFound("Фильм с идентификатором '%d' не найден".formatted(id)));
    }

    private void validateFilm(Film film) {
        if (film.getGenres() != null) {
            Set<Long> allGenreId = genreService.getGenres()
                    .stream()
                    .map(Genre::getId)
                    .collect(Collectors.toSet());
            Set<Long> allGenreIdFilm = film.getGenres()
                    .stream()
                    .map(Genre::getId)
                    .collect(Collectors.toSet());
            if (!allGenreId.containsAll(allGenreIdFilm)) {
                throw new ExceptionNotFound("Один или несколько жанров не существует.");
            }
        }
        if (film.getRating() != null && film.getRating().getId() != null) {
            Set<Long> ratingId = ratingService.getRatings()
                    .stream()
                    .map(Rating::getId)
                    .collect(Collectors.toSet());
            if (!ratingId.contains(film.getRating().getId())) {
                throw new ExceptionNotFound("Рейтинг с идентификатором %d не найден".formatted(film.getRating().getId()));
            }
        }
    }
}
