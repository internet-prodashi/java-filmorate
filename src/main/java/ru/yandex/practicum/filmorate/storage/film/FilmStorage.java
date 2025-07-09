package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface FilmStorage {

    Collection<Film> getFilms();

    Film createFilm(Film film);

    Optional<Film> updateFilm(Film film);

    void addLike(Long filmId, Long userId);

    Optional<Film> getFilmById(Long filmId);

    void removeLike(Long filmId, Long userId);

    List<Film> getPopularFilms(int count);

}