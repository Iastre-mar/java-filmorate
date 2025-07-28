package ru.yandex.practicum.filmorate.model;

import lombok.Data;

@Data
public class Review {
    private long id;
    private String content;
    private boolean isPositive;
    private long userId;
    private long filmId;
    private long useful = 0;
}
