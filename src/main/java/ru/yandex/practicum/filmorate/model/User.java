package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.time.LocalDate;

@Data
public class User {

    private int id;
    @NotBlank
    @Email
    private String email;
    @NotBlank
    @Pattern(regexp = "(?=\\S+$).+", message = "Логин не должен содержать пробелы.")
    private String login;
    private String name;
    @PastOrPresent
    private LocalDate birthday;

}
