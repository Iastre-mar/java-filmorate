package ru.yandex.practicum.filmorate.storage.impl.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exceptions.RatingNotFoundException;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.storage.RatingStorage;
import ru.yandex.practicum.filmorate.storage.mapper.RatingRowMapper;

import java.util.Collection;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class RatingDbStorage implements RatingStorage {
    private final JdbcTemplate jdbcTemplate;
    private final RatingRowMapper ratingRowMapper;

    @Override
    public Optional<Rating> get(Long id) {
        String sql = "SELECT id, code FROM ref_rating WHERE id = ?";
        Rating rating = null;
        try {
            rating = jdbcTemplate.queryForObject(sql, ratingRowMapper, id);

        } catch (EmptyResultDataAccessException e) {
            throw new RatingNotFoundException(
                    "Rating with id %d doesn't exist".formatted(id));
        }
        return Optional.ofNullable(rating);
    }

    @Override
    public Collection<Rating> getAll() {
        String sql = "SELECT id, code FROM ref_rating ORDER BY id";
        return jdbcTemplate.query(sql, ratingRowMapper);
    }
}