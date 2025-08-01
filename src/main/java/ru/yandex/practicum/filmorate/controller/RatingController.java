package ru.yandex.practicum.filmorate.controller;


import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.service.RatingService;

import java.util.ArrayList;
import java.util.Collection;

@RequiredArgsConstructor
@RestController
@RequestMapping("/mpa")
public class RatingController {

    private final RatingService ratingService;

    @GetMapping
    public Collection<Rating> getAllRatings() {
        return new ArrayList<>(ratingService.getAllRatings());
    }

    @GetMapping("/{id}")
    public Rating getRatingById(@PathVariable Long id) {
        return ratingService.getRatingOrThrow(id);
    }

}
