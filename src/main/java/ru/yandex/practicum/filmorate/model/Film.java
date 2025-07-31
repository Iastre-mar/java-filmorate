package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;
import ru.yandex.practicum.filmorate.util.DurationDeserializer;
import ru.yandex.practicum.filmorate.util.DurationSerializer;
import ru.yandex.practicum.filmorate.validation.AfterDate;
import ru.yandex.practicum.filmorate.validation.PositiveDuration;

import java.time.Duration;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Film.
 */
@Data
public class Film {
    public static final String START_OF_EPOCH = "1895-12-28";
    @EqualsAndHashCode.Exclude
    private Set<Long> setUserIdsLikedThis = new HashSet<>();
    private long id;
    @NotBlank private String name;
    @Size(max = 200) private String description;
    @AfterDate(START_OF_EPOCH) private LocalDate releaseDate;
    @NotNull @JsonSerialize(using = DurationSerializer.class)
    @JsonDeserialize(using = DurationDeserializer.class) @PositiveDuration
    private Duration duration;
    @JsonProperty("mpa") @Valid private Rating rating;
    @Valid private List<Genre> genres;
    private List<Director> directors;
}
