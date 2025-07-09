package ru.yandex.practicum.filmorate.storage.impl.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exceptions.FilmNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.mapper.FilmRowMapper;
import ru.yandex.practicum.filmorate.storage.mapper.RatingRowMapper;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.*;

@Repository
@RequiredArgsConstructor
public class FilmDbStorage implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;
    private final FilmRowMapper filmRowMapper;

    @Override
    public Collection<Film> getAll() {
        String sql = "SELECT * FROM films";
        List<Film> films = jdbcTemplate.query(sql, filmRowMapper);
        for (Film film : films) {
            loadLinkedFilmData(film);
        }
        return films;
    }

    @Override
    public Optional<Film> get(Long id) {
        String sql = "SELECT * FROM films WHERE id = ?";
        Film film = null;
        try {
            film = jdbcTemplate.queryForObject(sql, filmRowMapper, id);
            loadLinkedFilmData(film);
        } catch (EmptyResultDataAccessException e) {
            throw new FilmNotFoundException(
                    "Film with id %d doesn't exist".formatted(id));
        }
        return Optional.ofNullable(film);
    }

    @Override
    public Film persist(Film film) {
        String sql =
                "INSERT INTO films (name, description, release_date, duration, rating_id) " +
                "VALUES (?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql,
                                                               Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, film.getName());
            ps.setString(2, film.getDescription());
            ps.setDate(3, Date.valueOf(film.getReleaseDate()));
            ps.setInt(4, (int) film.getDuration()
                                   .toMinutes());
            ps.setLong(5, film.getRating()
                              .getId());
            return ps;
        }, keyHolder);

        Long generatedId = Objects.requireNonNull(keyHolder.getKey())
                                  .longValue();
        film.setId(generatedId);
        saveLinkedFilmData(film);
        return film;
    }

    @Override
    public Optional<Film> update(Film film) {
        String sql =
                "UPDATE films SET name = ?, description = ?, release_date = ?, " +
                "duration = ?, rating_id = ? WHERE id = ?";
        jdbcTemplate.update(sql, film.getName(), film.getDescription(),
                            Date.valueOf(film.getReleaseDate()),
                            film.getDuration(), film.getRating()
                                                    .getId(), film.getId());

        deleteLinkedFilmData(film);
        saveLinkedFilmData(film);
        return Optional.of(film);

    }

    @Override
    public Collection<Film> getTopFilms(Long count) {
        String sql = "SELECT f.*, COUNT(fl.user_id) AS likes_count " +
                     "FROM films f " +
                     "LEFT JOIN film_likes fl ON f.id = fl.film_id " +
                     "GROUP BY f.id " +
                     "ORDER BY likes_count DESC " +
                     "LIMIT ?";

        List<Film> films = jdbcTemplate.query(sql, filmRowMapper, count);
        for (Film film : films) {
            loadLinkedFilmData(film);
        }
        return films;
    }

    private void loadLinkedFilmData(Film film) {
        loadLikes(film);
        loadGenres(film);
        loadRating(film);
    }

    private void deleteLinkedFilmData(Film film) {
        deleteGenres(film);
        deleteLikes(film);
    }

    private void saveLinkedFilmData(Film film) {
        saveLikes(film);
        saveFilmGenres(film);
    }

    private void saveLikes(Film film) {

        String sql = "INSERT INTO film_likes (film_id, user_id) VALUES (?, ?)";
        List<Object[]> batchArgs = new HashSet<>(
                film.getSetUserIdsLikedThis()).stream()
                                              .map(userId -> new Object[]{
                                                      film.getId(), userId
                                              })
                                              .toList();

        jdbcTemplate.batchUpdate(sql, batchArgs);
    }

    private void deleteLikes(Film film) {
        String sql = "DELETE FROM film_likes WHERE film_id = ?";
        jdbcTemplate.update(sql, film.getId());
    }

    private void loadLikes(Film film) {
        String sql = "SELECT user_id FROM film_likes WHERE film_id = ?";
        List<Long> likes = jdbcTemplate.queryForList(sql, Long.class,
                                                     film.getId());
        film.setSetUserIdsLikedThis(new HashSet<>(likes));
    }

    private void loadRating(Film film) {
        String sql = "SELECT id, code FROM ref_rating WHERE id = ?";

        Rating rating = jdbcTemplate.queryForObject(sql, new RatingRowMapper(),
                                                    film.getRating()
                                                        .getId());
        film.setRating(rating);

    }

    private void saveFilmGenres(Film film) {
        String sql = "INSERT INTO film_genres (film_id, genre_id) VALUES (?, ?)";
        List<Object[]> batchArgs = new HashSet<>(film.getGenres()).stream()
                                                                  .map(genre -> new Object[]{
                                                                          film.getId(),
                                                                          genre.getId()
                                                                  })
                                                                  .toList();

        jdbcTemplate.batchUpdate(sql, batchArgs);
    }

    private void deleteGenres(Film film) {
        String sql = "DELETE FROM film_genres WHERE film_id = ?";
        jdbcTemplate.update(sql, film.getId());
    }

    private void loadGenres(Film film) {
        String sql = "SELECT g.id, g.name " +
                     "FROM ref_genre g " +
                     "JOIN film_genres fg ON g.id = fg.genre_id " +
                     "WHERE fg.film_id = ?";

        List<Genre> genres = jdbcTemplate.query(sql, (rs, rowNum) -> new Genre(
                rs.getLong("id"), rs.getString("name")), film.getId());

        film.setGenres(genres);
    }

}