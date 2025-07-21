package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import ru.yandex.practicum.filmorate.json.DeserializerDuration;
import ru.yandex.practicum.filmorate.json.SerializerDuration;
import ru.yandex.practicum.filmorate.validation.MinimumDate;
import ru.yandex.practicum.filmorate.validation.PositiveDuration;
import lombok.Data;

import java.time.Duration;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
public class Film {

    private Long id;
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
    private final Set<Long> likes = new HashSet<>();
    @JsonProperty("mpa")
    @Valid
    private Rating rating;
    @Valid
    private List<Genre> genres;
}
