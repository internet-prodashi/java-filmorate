package ru.yandex.practicum.filmorate.dao;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.ComponentScan;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.dal.dao.GenreDbStorage;

import java.util.Collection;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@ComponentScan(basePackages = {"ru.yandex.practicum.filmorate.storage"})
public class GenreDbStorageTest {
    private final GenreDbStorage genreDbStorage;

    @Test
    void getGenreById() {
        Optional<Genre> genre = genreDbStorage.getGenreById(1L);
        assertThat(genre).isPresent();
        assertThat(genre.get().getName()).isEqualTo("Комедия");

        Optional<Genre> genreNot = genreDbStorage.getGenreById(100L);
        assertThat(genreNot).isEmpty();
    }

    @Test
    void getGenres() {
        Collection<Genre> genres = genreDbStorage.getGenres();
        assertThat(genres).hasSize(6);
        assertThat(genres).extracting(Genre::getName)
                .containsExactlyInAnyOrder("Комедия", "Драма", "Мультфильм", "Триллер", "Документальный", "Боевик");
    }
}
