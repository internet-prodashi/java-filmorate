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
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Set;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class ValidationUserTest {

    private static Validator validator;
    private User user;


    @BeforeAll
    static void setupValidator() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }
    }

    @BeforeEach
    void setupValidUser() {
        user = new User();
        user.setId(1L);
        user.setEmail("internet-prodashi@yandex.ru");
        user.setLogin("internet-prodashi");
        user.setName("Ivan");
        user.setBirthday(LocalDate.of(1990, 2, 5));
    }

    @Test
    void validationEmailLoginAndBirthdayUser() {
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertThat(violations).isEmpty();
    }

    @ParameterizedTest
    @MethodSource("validEmail")
    void validationEmail(String email, boolean isValid) {
        user.setEmail(email);

        boolean isViolation = validator.validate(user)
                .stream()
                .anyMatch(a -> a.getPropertyPath().toString().equals("email"));

        assertThat(isViolation).isEqualTo(!isValid);
    }

    private static Stream<Arguments> validEmail() {
        return Stream.of(
                Arguments.of("internet-prodashi@yandex.ru", true),
                Arguments.of("  ", false),
                Arguments.of(" ", false),
                Arguments.of("", false),
                Arguments.of("internet prodashi@yandex.ru", false),
                Arguments.of("internet-prodashiyandex.ru", false),
                Arguments.of(null, false)
        );
    }

    @ParameterizedTest
    @MethodSource("validLogin")
    void validationLogin(String login, boolean isValid) {
        user.setLogin(login);

        boolean isViolation = validator.validate(user)
                .stream()
                .anyMatch(a -> a.getPropertyPath().toString().equals("login"));

        assertThat(isViolation).isEqualTo(!isValid);
    }

    private static Stream<Arguments> validLogin() {
        return Stream.of(
                Arguments.of("internet-prodashi", true),
                Arguments.of("internet prodashi", false),
                Arguments.of("  ", false),
                Arguments.of(" ", false),
                Arguments.of("", false),
                Arguments.of(null, false)
        );
    }

    @ParameterizedTest
    @MethodSource("validBirthday")
    void validationBirthday(LocalDate birthday, boolean isValid) {
        user.setBirthday(birthday);

        boolean isViolation = validator.validate(user)
                .stream()
                .anyMatch(a -> a.getPropertyPath().toString().equals("birthday"));

        assertThat(isViolation).isEqualTo(!isValid);
    }

    private static Stream<Arguments> validBirthday() {
        return Stream.of(
                Arguments.of(LocalDate.of(1990, 2, 5), true),
                Arguments.of(LocalDate.now(), true),
                Arguments.of(LocalDate.now().plusDays(1), false)
        );
    }

}