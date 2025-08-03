package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.RatingNotFoundException;
import ru.yandex.practicum.filmorate.logger.LogMethodResult;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.storage.RatingStorage;

import java.util.Collection;

@RequiredArgsConstructor
@Service
public class RatingService {

    private final RatingStorage ratingStorage;

    @LogMethodResult
    public Rating getRatingOrThrow(Long id) {
        return ratingStorage.get(id)
                .orElseThrow(() -> new RatingNotFoundException(
                        "Rating not found with id: " + id));
    }

    @LogMethodResult
    public Collection<Rating> getAllRatings() {
        return ratingStorage.getAll();
    }
}