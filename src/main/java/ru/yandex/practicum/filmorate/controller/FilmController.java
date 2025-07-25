package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.Collection;


@RequiredArgsConstructor
@RestController
@RequestMapping("/films")
public class FilmController {
    private final FilmService filmService;

    @GetMapping
    public Collection<Film> getFilms() {
        return filmService.getAll();
    }

    @GetMapping("{id}")
    public Film getFilm(@PathVariable Long id) {
        return filmService.getFilmByIdOrThrow(id)
                          .get();
    }

    @PostMapping
    public Film addFilm(@Valid @RequestBody Film film) {
        return filmService.add(film);
    }

    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film film) {
        return filmService.update(film)
                          .get();
    }

    @PutMapping("{id}/like/{userId}")
    public void likeFilm(@PathVariable Long id, @PathVariable Long userId) {
        filmService.addLikeToFilm(id, userId);
    }

    @DeleteMapping("{id}/like/{userId}")
    public void removeLikeFilm(@PathVariable Long id,
                               @PathVariable Long userId
    ) {
        filmService.removeLikeFromFilm(id, userId);
    }

    @GetMapping("/popular")
    public Collection<Film> getTopFilms(
            @RequestParam(required = false) Long count
    ) {
        if (count == null) {
            count = 10L;
        }
        return filmService.getTopFilms(count);
    }


}
