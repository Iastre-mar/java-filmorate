package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Review {
    @PositiveOrZero(message = "reviewId must be >= 0") private Long reviewId;
    @NotBlank(message = "content must not be blank") private String content;
    @JsonProperty("isPositive")
    @NotNull(message = "isPositive must not be null")
    private Boolean isPositive;
    @NotNull(message = "userId must not be null") private Long userId;
    @NotNull(message = "filmId must not be null") private Long filmId;
    @PositiveOrZero(message = "useful must be >= 0") private Long useful = 0L;
}
