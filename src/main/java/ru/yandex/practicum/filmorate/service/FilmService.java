package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ExceptionNotFound;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.util.Collection;
import java.util.List;

@RequiredArgsConstructor
@Service
public class FilmService {

    private final FilmStorage filmStorage;
    private final UserService userService;

    public Collection<Film> getFilms() {
        return filmStorage.getFilms();
    }

    public Film createFilm(Film film) {
        return filmStorage.createFilm(film);
    }

    public Film updateFilm(Film film) {
        return filmStorage.updateFilm(film)
                .orElseThrow(() -> new ExceptionNotFound("Фильм с идентификатором '%d' не найден".formatted(film.getId())));
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

}
