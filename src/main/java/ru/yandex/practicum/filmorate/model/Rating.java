package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class Rating {
    @NotNull private Long id;
    private String name;
}