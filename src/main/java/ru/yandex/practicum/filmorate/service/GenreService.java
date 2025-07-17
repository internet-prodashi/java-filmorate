package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ExceptionNotFound;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.dal.dao.GenreDbStorage;

import java.util.Collection;

@RequiredArgsConstructor
@Service
public class GenreService {
    private final GenreDbStorage genreDbStorage;

    public Genre getGenreById(Long id) {
        return genreDbStorage.getGenreById(id)
                .orElseThrow(() -> new ExceptionNotFound("Жанр с идентификатором '%d' не найден".formatted(id)));
    }

    public Collection<Genre> getGenres() {
        return genreDbStorage.getGenres();
    }
}
