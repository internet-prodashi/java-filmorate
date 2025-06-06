package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import ru.yandex.practicum.filmorate.json.DeserializerDuration;
import ru.yandex.practicum.filmorate.json.SerializerDuration;
import ru.yandex.practicum.filmorate.validation.MinimumDate;
import ru.yandex.practicum.filmorate.validation.PositiveDuration;
import lombok.Data;

import java.time.Duration;
import java.time.LocalDate;

@Data
public class Film {

    private int id;
    @NotBlank
    private String name;
    @Size(max = 200)
    private String description;
    @MinimumDate
    private LocalDate releaseDate;
    @PositiveDuration
    @JsonSerialize(using = SerializerDuration.class)
    @JsonDeserialize(using = DeserializerDuration.class)
    private Duration duration;

}
