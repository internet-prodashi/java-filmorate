package ru.yandex.practicum.filmorate;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.Duration;
import java.time.LocalDate;
import java.util.Set;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class ValidationFilmTest {

    private static Validator validator;
    private Film film;

    @BeforeAll
    static void setupValidator() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }
    }

    @BeforeEach
    void setupValidFilm() {
        film = new Film();
        film.setId(1L);
        film.setName("Name Film");
        film.setDescription("Description Film");
        film.setReleaseDate(LocalDate.of(2025, 6, 1));
        film.setDuration(Duration.ofMinutes(120));
    }

    @Test
    void validationNameDescriptionDateDurationFilm() {
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertThat(violations).isEmpty();
    }


    @ParameterizedTest
    @MethodSource("validName")
    void validationName(String name, boolean isValid) {
        film.setName(name);

        boolean isViolation = validator.validate(film)
                .stream()
                .anyMatch(a -> a.getPropertyPath().toString().equals("name"));

        assertThat(isViolation).isEqualTo(!isValid);
    }

    private static Stream<Arguments> validName() {
        return Stream.of(
                Arguments.of("Name", true),
                Arguments.of("Name Film", true),
                Arguments.of("Name-Film", true),
                Arguments.of("Name_Film", true),
                Arguments.of("Название фильма", true),
                Arguments.of("  ", false),
                Arguments.of(" ", false),
                Arguments.of("", false),
                Arguments.of(null, false)
        );
    }

    @ParameterizedTest
    @MethodSource("validDescription")
    void validationDescriptionMax200Characters(String description, boolean isValid) {
        film.setDescription(description);

        boolean isViolation = validator.validate(film)
                .stream()
                .anyMatch(a -> a.getPropertyPath().toString().equals("description"));

        assertThat(isViolation).isEqualTo(!isValid);
    }

    private static Stream<Arguments> validDescription() {
        return Stream.of(
                Arguments.of("Name Film", true),
                Arguments.of("Name Film ".repeat(20), true),
                Arguments.of("Name Film ".repeat(20) + "!", false)
        );
    }

    @ParameterizedTest
    @MethodSource("validReleaseDate")
    void validationReleaseDate(LocalDate date, boolean isValid) {
        film.setReleaseDate(date);

        boolean isViolation = validator.validate(film)
                .stream()
                .anyMatch(a -> a.getPropertyPath().toString().equals("releaseDate"));

        assertThat(isViolation).isEqualTo(!isValid);
    }

    private static Stream<Arguments> validReleaseDate() {
        return Stream.of(
                Arguments.of(LocalDate.of(1895, 12, 29), true),
                Arguments.of(LocalDate.of(1895, 12, 28), true),
                Arguments.of(LocalDate.of(1895, 12, 27), false)
        );
    }

    @ParameterizedTest
    @MethodSource("validDurations")
    void validationDuration(Duration duration, boolean isValid) {
        film.setDuration(duration);

        boolean isViolation = validator.validate(film)
                .stream()
                .anyMatch(a -> a.getPropertyPath().toString().equals("duration"));

        assertThat(isViolation).isEqualTo(!isValid);
    }

    private static Stream<Arguments> validDurations() {
        return Stream.of(
                Arguments.of(Duration.ofMinutes(1), true),
                Arguments.of(Duration.ZERO, false),
                Arguments.of(Duration.ofMinutes(-1), false)
        );
    }

}