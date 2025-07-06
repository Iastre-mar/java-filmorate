package ru.yandex.practicum.filmorate.storage.impl.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.mapper.FilmRowMapper;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.*;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class FilmDbStorage implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;
    private final FilmRowMapper filmRowMapper;

    @Override
    public Collection<Film> getAll() {
        String sql = "SELECT * FROM films";
        return jdbcTemplate.query(sql, filmRowMapper);
    }

    @Override
    public Optional<Film> get(Long id) {
        String sql = "SELECT * FROM films WHERE id = ?";
        Film film = null;
        try {
            film = jdbcTemplate.queryForObject(sql, filmRowMapper, id);
            loadLikes(film);
            loadGenres(film);
        } catch (EmptyResultDataAccessException e) {
            // Думаю что сюда засунуть
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
        saveLikes(film);
        saveFilmGenres(film);
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

        deleteLikes(film.getId());
        saveLikes(film);

        deleteGenres(film.getId());
        saveFilmGenres(film);
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

        return jdbcTemplate.query(sql, filmRowMapper, count);
    }

    private void saveLikes(Film film) {

        String sql = "INSERT INTO film_likes (film_id, user_id) VALUES (?, ?)";
        List<Object[]> batchArgs = film.getSetUserIdsLikedThis()
                                       .stream()
                                       .map(userId -> new Object[]{
                                               film.getId(), userId
                                       })
                                       .toList();

        jdbcTemplate.batchUpdate(sql, batchArgs);
    }

    private void deleteLikes(Long filmId) {
        String sql = "DELETE FROM film_likes WHERE film_id = ?";
        jdbcTemplate.update(sql, filmId);
    }

    private void loadLikes(Film film) {
        String sql = "SELECT user_id FROM film_likes WHERE film_id = ?";
        List<Long> likes = jdbcTemplate.queryForList(sql, Long.class,
                                                     film.getId());
        film.setSetUserIdsLikedThis(new HashSet<>(likes));
    }

    private void saveFilmGenres(Film film) {
        String sql = "INSERT INTO film_genres (film_id, genre_id) VALUES (?, ?)";
        List<Object[]> batchArgs = film.getGenres()
                                       .stream()
                                       .map(genre -> new Object[]{
                                               film.getId(), genre.getId()
                                       })
                                       .collect(Collectors.toList());

        jdbcTemplate.batchUpdate(sql, batchArgs);
    }

    private void deleteGenres(Long filmId) {
        String sql = "DELETE FROM film_genres WHERE film_id = ?";
        jdbcTemplate.update(sql, filmId);
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