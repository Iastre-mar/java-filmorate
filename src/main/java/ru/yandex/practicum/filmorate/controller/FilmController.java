package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.Collection;
import java.util.List;


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
    public Collection<Film> getTopFilms(@RequestParam(required = false) Long count,
            @RequestParam(required = false) Long genreId,
            @RequestParam(required = false) Integer year
    ) {
        if (count == null) {
            count = 10L;
        }
        if (genreId != null && year != null) {
            return filmService.getTopFilmsByGenreAndYear(count, genreId, year);
        } else if (genreId != null) {
            return filmService.getTopFilms(count, genreId, null);
        } else if (year != null) {
            return filmService.getTopFilms(count, null, year);
        } else {
            return filmService.getTopFilms(count, null, null);
        }
    }

    @GetMapping("/common")
    public Collection<Film> getCommonFilms(@RequestParam Long userId, @RequestParam Long friendId) {
        return filmService.getCommonFilms(userId, friendId);
    }


    @GetMapping("/director/{directorId}")
    public Collection<Film> getDirectorFilms(@PathVariable Long directorId,
                                             @RequestParam String sortBy
    ) {
        if (!sortBy.equals("year") && !sortBy.equals("likes")) {
            sortBy = "year";
        }
        return filmService.getDirectorFilms(directorId, sortBy);
    }

    @GetMapping("/search")
    public Collection<Film> getFilmsSearch(@RequestParam String query,
                                           @RequestParam List<String> by
    ) {
        return filmService.getFilmsSearch(query, by);
    }
}
