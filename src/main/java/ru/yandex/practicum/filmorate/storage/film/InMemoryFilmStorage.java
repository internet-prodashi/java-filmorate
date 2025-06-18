package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Component
public class InMemoryFilmStorage implements FilmStorage {

    private final Map<Long, Film> films = new HashMap<>();
    private Long id = 1L;

    @Override
    public Collection<Film> getFilms() {
        log.trace("Вызван метод getFilms");
        try {
            log.trace("Успешно выполнен метод getFilms");
            return films.values();
        } catch (RuntimeException e) {
            log.error("Ошибка в методе getFilms: {}", e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public Film createFilm(Film film) {
        log.trace("Вызван метод createFilm");
        try {
            log.trace("Успешно выполнен метод createFilm");
            film.setId(generateId());
            films.put(film.getId(), film);
            return film;
        } catch (RuntimeException e) {
            log.error("Ошибка в методе createFilm: {}", e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public Optional<Film> updateFilm(Film film) {
        log.trace("Вызван метод updateFilm");
        try {
            log.trace("Успешно выполнен метод updateFilm");
            return Optional.ofNullable(films.computeIfPresent(film.getId(), (a, b) -> film));
        } catch (RuntimeException e) {
            log.error("Ошибка в методе updateFilm: {}", e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public void addLike(Long filmId, Long userId) {
        log.trace("Вызван метод addLike");
        try {
            log.trace("Успешно выполнен метод addLike");
            films.get(filmId).getLikes().add(userId);
        } catch (RuntimeException e) {
            log.error("Ошибка в методе addLike: {}", e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public Optional<Film> getFilmById(Long id) {
        log.trace("Вызван метод getFilmById");
        try {
            log.trace("Успешно выполнен метод getFilmById");
            return Optional.ofNullable(films.get(id));
        } catch (RuntimeException e) {
            log.error("Ошибка в методе getFilmById: {}", e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public void removeLike(Long filmId, Long userId) {
        log.trace("Вызван метод removeLike");
        try {
            log.trace("Успешно выполнен метод removeLike");
            films.get(filmId).getLikes().remove(userId);
        } catch (RuntimeException e) {
            log.error("Ошибка в методе removeLike: {}", e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public List<Film> getPopularFilms(int count) {
        log.trace("Вызван метод getPopularFilms");
        try {
            log.trace("Успешно выполнен метод getPopularFilms");
            return films.entrySet()
                    .stream()
                    .collect(
                            Collectors.toMap(
                                    Map.Entry::getKey,
                                    filmEntry -> filmEntry.getValue().getLikes().size()
                            )
                    )
                    .entrySet()
                    .stream()
                    .sorted(Map.Entry.<Long, Integer>comparingByValue().reversed())
                    .limit(count)
                    .map(entry -> films.get(entry.getKey()))
                    .collect(Collectors.toList());
        } catch (RuntimeException e) {
            log.error("Ошибка в методе getPopularFilms: {}", e.getMessage(), e);
            throw e;
        }
    }

    private Long generateId() {
        return id++;
    }

}