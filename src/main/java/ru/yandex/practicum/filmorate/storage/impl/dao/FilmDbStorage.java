package ru.yandex.practicum.filmorate.storage.impl.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exceptions.FilmNotFoundException;
import ru.yandex.practicum.filmorate.model.Director;
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
import java.util.function.Function;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class FilmDbStorage implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    private final FilmRowMapper filmRowMapper;
    private final RatingRowMapper ratingRowMapper;

    @Override
    public Collection<Film> getAll() {
        String sql = "SELECT * FROM films";
        List<Film> films = jdbcTemplate.query(sql, filmRowMapper);
        loadLinkedDataForBatch(films);
        return films;
    }

    @Override
    public Optional<Film> get(Long id) {
        String sql = "SELECT * FROM films WHERE id = ?";
        try {
            Film film = jdbcTemplate.queryForObject(sql, filmRowMapper, id);
            loadLinkedDataForBatch(Collections.singletonList(film));
            return Optional.ofNullable(film);
        } catch (EmptyResultDataAccessException e) {
            throw new FilmNotFoundException(
                    "Film with id %d doesn't exist".formatted(id));
        }
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
                            film.getDuration()
                                .toMinutes(), film.getRating()
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
        loadLinkedDataForBatch(films);
        return films;
    }

    @Override
    public Collection<Film> getDirectorFilms(Long directorId, String sortBy) {
        String sql = "SELECT f.* " +
                     "FROM films f " +
                     "JOIN film_directors fd ON f.id = fd.film_id " +
                     "JOIN ref_director d ON fd.director_id = d.id " +
                     "WHERE d.id = ? " +
                     "ORDER BY EXTRACT(YEAR FROM f.release_date);";
        if (sortBy.equals("likes")) {
            sql = "SELECT f.* " +
                  "FROM films f " +
                  "JOIN film_directors fd ON f.id = fd.film_id " +
                  "JOIN ref_director d ON fd.director_id = d.id " +
                  "LEFT JOIN film_likes fl ON f.id = fl.film_id " +
                  "WHERE d.id = ? " +
                  "GROUP BY f.id " +
                  "ORDER BY COUNT(fl.film_id) DESC;";
        }

        List<Film> films = jdbcTemplate.query(sql, filmRowMapper, directorId);
        loadLinkedDataForBatch(films);
        return films;
    }

    @Override
    public Collection<Film> getFilmsSearch(String query, List<String> by) {
        String sql = "SELECT f.* " +
                "FROM films f " +
                "LEFT JOIN film_likes fl ON f.id = fl.film_id " +
                "WHERE f.name ILIKE ? " +
                "GROUP BY f.id " +
                "ORDER BY COUNT(fl.film_id) DESC;";
        List<Film> films = new ArrayList<>();
        if (!by.isEmpty()) {
            for (String sortType : by) {
                if (sortType.equals("director")) {
                    sql = "SELECT f.* " +
                            "FROM films f " +
                            "LEFT JOIN film_directors fd ON f.id = fd.film_id " +
                            "LEFT JOIN ref_director d ON fd.director_id = d.id " +
                            "WHERE d.name ILIKE ? " +
                            "GROUP BY f.id, d.id " +
                            "ORDER BY f.name;";
                } else if (sortType.equals("title")) {
                    sql = "SELECT * " +
                            "FROM films " +
                            "WHERE name ILIKE ? " +
                            "ORDER BY name;";
                }
                films.addAll(jdbcTemplate.query(sql, filmRowMapper, "%" + query + "%"));
            }
        } else {
            films.addAll(jdbcTemplate.query(sql, filmRowMapper, "%" + query + "%"));
        }
        loadLinkedDataForBatch(films);
        return films;
    }

    private void loadLinkedDataForBatch(List<Film> films) {

        Map<Long, Film> filmMap = films.stream()
                                       .collect(Collectors.toMap(Film::getId,
                                                                 Function.identity()));

        Set<Long> filmIds = filmMap.keySet();
        Set<Long> ratingIds = films.stream()
                                   .map(f -> f.getRating()
                                              .getId())
                                   .collect(Collectors.toSet());
        Map<Long, Set<Long>> likesMap = loadLikesForFilms(filmIds);

        Map<Long, List<Genre>> genresMap = loadGenresForFilms(filmIds);

        Map<Long, List<Director>> directorsMap = loadDirectorsForFilms(
                filmIds);

        Map<Long, Rating> ratingsMap = loadRatingsByIds(ratingIds);

        for (Film film : films) {
            film.setSetUserIdsLikedThis(
                    likesMap.getOrDefault(film.getId(), new HashSet<>()));
            film.setGenres(
                    genresMap.getOrDefault(film.getId(), new ArrayList<>()));
            film.setDirectors(directorsMap.getOrDefault(film.getId(),
                                                        new ArrayList<>()));

            Rating fullRating = ratingsMap.get(film.getRating()
                                                   .getId());
            film.setRating(fullRating);
        }
    }

    private Map<Long, Set<Long>> loadLikesForFilms(Set<Long> filmIds) {
        String sql = "SELECT film_id, user_id FROM film_likes WHERE film_id IN (:filmIds)";
        Map<String, Object> params = Collections.singletonMap("filmIds",
                                                              filmIds);

        return namedParameterJdbcTemplate.query(sql, params, rs -> {
            Map<Long, Set<Long>> result = new HashMap<>();
            while (rs.next()) {
                Long filmId = rs.getLong("film_id");
                Long userId = rs.getLong("user_id");
                result.computeIfAbsent(filmId, k -> new HashSet<>())
                      .add(userId);
            }
            return result;
        });
    }

    private Map<Long, List<Genre>> loadGenresForFilms(Set<Long> filmIds) {
        String sql = "SELECT fg.film_id, g.id, g.name " +
                     "FROM film_genres fg " +
                     "JOIN ref_genre g ON fg.genre_id = g.id " +
                     "WHERE fg.film_id IN (:filmIds)";

        Map<String, Object> params = Collections.singletonMap("filmIds",
                                                              filmIds);

        return namedParameterJdbcTemplate.query(sql, params, rs -> {
            Map<Long, List<Genre>> result = new HashMap<>();
            while (rs.next()) {
                Long filmId = rs.getLong("film_id");
                Long genreId = rs.getLong("id");
                String genreName = rs.getString("name");
                result.computeIfAbsent(filmId, k -> new ArrayList<>())
                      .add(new Genre(genreId, genreName));
            }
            return result;
        });
    }

    private Map<Long, List<Director>> loadDirectorsForFilms(Set<Long> filmIds) {
        String sql = "SELECT fd.film_id, d.id, d.name " +
                     "FROM film_directors fd " +
                     "JOIN ref_director d ON fd.director_id = d.id " +
                     "WHERE fd.film_id IN (:filmIds)";

        Map<String, Object> params = Collections.singletonMap("filmIds",
                                                              filmIds);

        return namedParameterJdbcTemplate.query(sql, params, rs -> {
            Map<Long, List<Director>> result = new HashMap<>();
            while (rs.next()) {
                Long filmId = rs.getLong("film_id");
                Long directorId = rs.getLong("id");
                String directorName = rs.getString("name");
                result.computeIfAbsent(filmId, k -> new ArrayList<>());
                Director director = new Director(directorId, directorName);
                if (!result.get(filmId)
                           .contains(director)) {
                    result.get(filmId)
                          .add(director);
                }
            }
            return result;
        });
    }

    private Map<Long, Rating> loadRatingsByIds(Set<Long> ratingIds) {
        String sql = "SELECT id, code FROM ref_rating WHERE id IN (:ratingIds)";
        Map<String, Object> params = Collections.singletonMap("ratingIds",
                                                              ratingIds);

        List<Rating> ratings = namedParameterJdbcTemplate.query(sql, params,
                                                                ratingRowMapper);

        return ratings.stream()
                      .collect(Collectors.toMap(Rating::getId,
                                                Function.identity()));
    }

    public void saveLinkedFilmData(Film film) {
        saveLikes(film);
        saveFilmGenres(film);
        saveFilmDirectors(film);
    }

    private void deleteLinkedFilmData(Film film) {
        deleteGenres(film);
        deleteLikes(film);
        deleteFilmDirectors(film);
    }

    private void saveLikes(Film film) {
        String sql = "MERGE INTO film_likes (film_id, user_id) VALUES (?, ?)";
        Set<Long> likes = film.getSetUserIdsLikedThis();
        if (likes == null || likes.isEmpty())
            return;

        List<Object[]> batchArgs = likes.stream()
                                        .map(userId -> new Object[]{
                                                film.getId(), userId
                                        })
                                        .collect(Collectors.toList());

        jdbcTemplate.batchUpdate(sql, batchArgs);
    }

    private void deleteLikes(Film film) {
        String sql = "DELETE FROM film_likes WHERE film_id = ?";
        jdbcTemplate.update(sql, film.getId());
    }

    private void saveFilmGenres(Film film) {
        String sql = "MERGE INTO film_genres (film_id, genre_id) KEY (film_id, genre_id) VALUES (?, ?)";

        List<Genre> genres = film.getGenres();
        if (genres == null || genres.isEmpty()) {
            return;
        }

        Set<Long> uniqueGenreIds = genres.stream()
                                         .map(Genre::getId)
                                         .collect(Collectors.toSet());

        List<Object[]> batchArgs = uniqueGenreIds.stream()
                                                 .map(genreId -> new Object[]{
                                                         film.getId(),
                                                         genreId
                                                 })
                                                 .collect(Collectors.toList());

        jdbcTemplate.batchUpdate(sql, batchArgs);
    }

    private void deleteGenres(Film film) {
        String sql = "DELETE FROM film_genres WHERE film_id = ?";
        jdbcTemplate.update(sql, film.getId());
    }

    private void deleteFilmDirectors(Film film) {
        String sql = "DELETE FROM film_directors WHERE film_id = ?";
        jdbcTemplate.update(sql, film.getId());
    }

    private void saveFilmDirectors(Film film) {
        String sql = "INSERT INTO film_directors (film_id, director_id) VALUES (?, ?)";
        List<Director> directors = film.getDirectors();
        if (directors == null || directors.isEmpty())
            return;

        Set<Long> uniqueDirectorIds = film.getDirectors()
                                          .stream()
                                          .map(Director::getId)
                                          .collect(Collectors.toSet());

        List<Object[]> batchArgs = uniqueDirectorIds.stream()
                                                    .map(directorId -> new Object[]{
                                                            film.getId(),
                                                            directorId
                                                    })
                                                    .toList();

        jdbcTemplate.batchUpdate(sql, batchArgs);
    }
}