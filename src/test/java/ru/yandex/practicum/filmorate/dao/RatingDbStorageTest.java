package ru.yandex.practicum.filmorate.dao;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.ComponentScan;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.storage.dal.dao.RatingDbStorage;

import java.util.Collection;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@ComponentScan(basePackages = {"ru.yandex.practicum.filmorate.storage"})
public class RatingDbStorageTest {
    private final RatingDbStorage ratingDbStorage;

    @Test
    void getRatingById() {
        Optional<Rating> rating = ratingDbStorage.getRatingById(1L);
        assertThat(rating).isPresent();
        assertThat(rating.get().getName()).isEqualTo("G");

        Optional<Rating> ratingNot = ratingDbStorage.getRatingById(100L);
        assertThat(ratingNot).isEmpty();
    }

    @Test
    void getRatings() {
        Collection<Rating> ratings = ratingDbStorage.getRatings();
        assertThat(ratings).hasSize(5);
        assertThat(ratings).extracting(Rating::getName)
                .containsExactlyInAnyOrder("G", "PG", "PG-13", "R", "NC-17");
    }

}
