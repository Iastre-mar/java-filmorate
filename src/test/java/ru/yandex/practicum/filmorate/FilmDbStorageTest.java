package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.jdbc.Sql;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.storage.impl.dao.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.mapper.FilmRowMapper;
import ru.yandex.practicum.filmorate.storage.mapper.RatingRowMapper;

import java.time.Duration;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import({FilmDbStorage.class, FilmRowMapper.class, RatingRowMapper.class})
@Sql(scripts = {"/schema.sql", "/test-data.sql"},
     executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class FilmDbStorageTest {

    private final JdbcTemplate jdbcTemplate;
    private final FilmDbStorage filmDbStorage;

    @Test
    void getAll_shouldReturnAllFilmsWithLinkedData() {
        Collection<Film> films = filmDbStorage.getAll();
        assertThat(films).hasSize(2);

        Film film1 = films.stream()
                          .filter(f -> f.getId() == 1)
                          .findFirst()
                          .get();
        assertThat(film1.getName()).isEqualTo("Film 1");
        assertThat(film1.getRating()
                        .getName()).isEqualTo("PG-13");
        assertThat(film1.getGenres()).extracting(Genre::getName)
                                     .containsExactlyInAnyOrder("Комедия",
                                                                "Драма");
        assertThat(film1.getSetUserIdsLikedThis()).containsExactlyInAnyOrder(
                101L, 102L);

        Film film2 = films.stream()
                          .filter(f -> f.getId() == 2)
                          .findFirst()
                          .get();
        assertThat(film2.getName()).isEqualTo("Film 2");
    }

    @Test
    void get_shouldReturnFilmByIdWithAllData() {

        Optional<Film> filmOptional = filmDbStorage.get(1L);

        assertThat(filmOptional).isPresent();

        Film film = filmOptional.get();
        assertThat(film.getName()).isEqualTo("Film 1");
        assertThat(film.getDescription()).isEqualTo("Description 1");
        assertThat(film.getReleaseDate()).isEqualTo(LocalDate.of(2020, 1, 1));
        assertThat(film.getDuration()
                       .toMinutes()).isEqualTo(120);
        assertThat(film.getRating()
                       .getName()).isEqualTo("PG-13");
        assertThat(film.getGenres()).extracting(Genre::getName)
                                    .containsExactlyInAnyOrder("Комедия",
                                                               "Драма");
        assertThat(film.getSetUserIdsLikedThis()).containsExactlyInAnyOrder(
                101L, 102L);
    }

    @Test
    void persist_shouldSaveFilmWithLinkedData() {
        Film newFilm = new Film();
        newFilm.setName("New Film");
        newFilm.setDescription("New Description");
        newFilm.setReleaseDate(LocalDate.of(2023, 1, 1));
        newFilm.setDuration(Duration.ofMinutes(90));

        Rating rating = new Rating();
        rating.setId(4L); // R rating
        newFilm.setRating(rating);

        newFilm.setGenres(List.of(new Genre(1L, "Комедия"),
                                  new Genre(3L, "Мультфильм")));
        newFilm.setSetUserIdsLikedThis(Set.of(103L, 104L));

        Film savedFilm = filmDbStorage.persist(newFilm);

        assertThat(savedFilm.getId()).isNotNull();

        Optional<Film> retrievedFilm = filmDbStorage.get(savedFilm.getId());
        assertThat(retrievedFilm).isPresent();

        Film film = retrievedFilm.get();
        assertThat(film.getName()).isEqualTo("New Film");
        assertThat(film.getRating()
                       .getName()).isEqualTo("R");
        assertThat(film.getGenres()).extracting(Genre::getName)
                                    .containsExactlyInAnyOrder("Комедия",
                                                               "Мультфильм");
        assertThat(film.getSetUserIdsLikedThis()).containsExactlyInAnyOrder(
                103L, 104L);
    }

    @Test
    void update_shouldUpdateFilmAndLinkedData() {
        Film film = filmDbStorage.get(1L)
                                 .get();
        film.setName("Updated Film");
        film.setDescription("Updated Description");

        Rating newRating = new Rating();
        newRating.setId(4L); // R rating
        film.setRating(newRating);
        film.setGenres(List.of(new Genre(1L, "Комедия"),
                               new Genre(3L, "Мультфильм")));

        film.setSetUserIdsLikedThis(Set.of(101L, 103L));

        Optional<Film> updatedFilm = filmDbStorage.update(film);

        assertThat(updatedFilm).isPresent();

        Film retrievedFilm = filmDbStorage.get(1L)
                                          .get();
        assertThat(retrievedFilm.getName()).isEqualTo("Updated Film");
        assertThat(retrievedFilm.getDescription()).isEqualTo(
                "Updated Description");
        assertThat(retrievedFilm.getRating()
                                .getName()).isEqualTo("R");
        assertThat(retrievedFilm.getGenres()).extracting(Genre::getName)
                                             .containsExactlyInAnyOrder(
                                                     "Комедия", "Мультфильм");
        assertThat(
                retrievedFilm.getSetUserIdsLikedThis()).containsExactlyInAnyOrder(
                101L, 103L);
    }

    @Test
    void getTopFilms_shouldReturnFilmsOrderedByLikes() {
        jdbcTemplate.update("DELETE FROM film_likes");

        jdbcTemplate.update("INSERT INTO film_likes (film_id, user_id) VALUES (1, 101)");
        jdbcTemplate.update("INSERT INTO film_likes (film_id, user_id) VALUES (1, 102)");
        jdbcTemplate.update("INSERT INTO film_likes (film_id, user_id) VALUES (2, 103)");
        jdbcTemplate.update("INSERT INTO film_likes (film_id, user_id) VALUES (2, 104)");
        jdbcTemplate.update("INSERT INTO film_likes (film_id, user_id) VALUES (2, 105)");

        Collection<Film> topFilms = filmDbStorage.getTopFilms(2L, null, null);

        assertThat(topFilms).hasSize(2);

        Film firstFilm = topFilms.iterator().next();
        assertThat(firstFilm.getId()).isEqualTo(2L);
        assertThat(firstFilm.getSetUserIdsLikedThis()).hasSize(3);

        Film secondFilm = topFilms.stream().skip(1).findFirst().get();
        assertThat(secondFilm.getId()).isEqualTo(1L);
        assertThat(secondFilm.getSetUserIdsLikedThis()).hasSize(2);
    }

    @Test
    void persist_shouldHandleDuplicateGenres() {
        Film newFilm = new Film();
        newFilm.setName("Film with Duplicate Genres");
        newFilm.setReleaseDate(LocalDate.now());
        newFilm.setDuration(Duration.ofMinutes(100));

        Rating rating = new Rating();
        rating.setId(1L);
        newFilm.setRating(rating);

        newFilm.setGenres(
                List.of(new Genre(1L, "Комедия"), new Genre(1L, "Комедия"),
                        new Genre(2L, "Драма")));

        Film savedFilm = filmDbStorage.persist(newFilm);

        Optional<Film> retrievedFilm = filmDbStorage.get(savedFilm.getId());
        assertThat(retrievedFilm).isPresent();
        assertThat(retrievedFilm.get()
                                .getGenres()).extracting(Genre::getId)
                                             .containsExactlyInAnyOrder(1L,
                                                                        2L);
    }

    @Test
    void getTopFilmsByGenreAndYear() {
        jdbcTemplate.update("DELETE FROM film_likes");
        jdbcTemplate.update("INSERT INTO film_likes (film_id, user_id) VALUES (1, 101)");
        jdbcTemplate.update("INSERT INTO film_likes (film_id, user_id) VALUES (1, 102)");
        jdbcTemplate.update("INSERT INTO film_likes (film_id, user_id) VALUES (2, 103)");
        jdbcTemplate.update("INSERT INTO film_genres (film_id, genre_id) VALUES (1, 1)");
        jdbcTemplate.update("INSERT INTO film_genres (film_id, genre_id) VALUES (2, 1)");
        jdbcTemplate.update("INSERT INTO films (id, name, description, release_date, duration, rating_id) VALUES (3, 'Film 3', 'Description 3', '2021-01-01', 150, 1)");
        jdbcTemplate.update("INSERT INTO film_genres (film_id, genre_id) VALUES (3, 1)");
        jdbcTemplate.update("INSERT INTO film_likes (film_id, user_id) VALUES (3, 104)");
        jdbcTemplate.update("INSERT INTO film_likes (film_id, user_id) VALUES (3, 105)");
        jdbcTemplate.update("INSERT INTO film_likes (film_id, user_id) VALUES (3, 106)");

        Collection<Film> topFilms = filmDbStorage.getTopFilms(2L, 1L, 2021);
        assertThat(topFilms).hasSize(1);
        Film firstFilm = topFilms.iterator().next();
        assertThat(firstFilm.getId()).isEqualTo(3L);
        assertThat(firstFilm.getSetUserIdsLikedThis()).hasSize(3);
    }
}