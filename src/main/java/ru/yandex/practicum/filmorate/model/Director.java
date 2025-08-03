package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class Director {
    private Long id;
    @NotBlank
    private String name;


    public Director(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public Director() {
    }

    public Director(Long id) {
        this.id = id;
    }
}
