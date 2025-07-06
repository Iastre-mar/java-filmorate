package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@JsonIgnoreProperties(ignoreUnknown = true)
@Setter
@Getter
public class Rating {
    @NotNull
    private Long id;
    private String name;
}