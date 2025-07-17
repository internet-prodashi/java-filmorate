package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ExceptionNotFound;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.storage.dal.dao.RatingDbStorage;

import java.util.Collection;

@RequiredArgsConstructor
@Service
public class RatingService {
    private final RatingDbStorage ratingDbStorage;

    public Rating getRatingById(Long id) {
        return ratingDbStorage.getRatingById(id)
                .orElseThrow(() -> new ExceptionNotFound("Рейтинг с идентификатором '%d' не найден".formatted(id)));
    }

    public Collection<Rating> getRatings() {
        return ratingDbStorage.getRatings();
    }
}
