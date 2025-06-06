package ru.yandex.practicum.filmorate.manager;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Component
public class InMemoryFilmManager {

    private final Map<Integer, Film> films = new HashMap<>();
    private int id = 1;

    public Collection<Film> getFilms() {
        return films.values();
    }

    public Film createFilm(Film film) {
        log.trace("Вызван метод createFilm");
        try {
            film.setId(generateId());
            films.put(film.getId(), film);
            log.trace("Успешно выполнен метод createFilm");
            return film;
        } catch (RuntimeException e) {
            log.error("Ошибка в методе createFilm: {}", e.getMessage(), e);
            throw e;
        }
    }

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

    private int generateId() {
        return id++;
    }

}