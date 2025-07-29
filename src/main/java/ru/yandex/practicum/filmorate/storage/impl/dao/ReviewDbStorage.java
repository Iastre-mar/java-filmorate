package ru.yandex.practicum.filmorate.storage.impl.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exceptions.ReviewNotFoundException;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.ReviewStorage;
import ru.yandex.practicum.filmorate.storage.mapper.ReviewRowMapper;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.Collection;
import java.util.Objects;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ReviewDbStorage implements ReviewStorage {
    private final JdbcTemplate jdbcTemplate;
    private final ReviewRowMapper reviewRowMapper;

    @Override
    public Review persist(Review review) {
        String sql =
                "INSERT INTO reviews (content, is_positive, user_id, film_id, useful) " +
                "VALUES (?, ?, ?, ?, 0)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        System.out.println(review);

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql,
                                                               Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, review.getContent());
            ps.setBoolean(2, review.isPositive());
            ps.setLong(3, review.getUserId());
            ps.setLong(4, review.getFilmId());
            return ps;
        }, keyHolder);

        Long generatedId = Objects.requireNonNull(keyHolder.getKey())
                                  .longValue();
        review.setId(generatedId);
        return review;
    }

    @Override
    public Optional<Review> update(Review review) {
        String sql = "UPDATE reviews SET content = ?, is_positive = ? WHERE id = ?";
        jdbcTemplate.update(sql, review.getContent(), review.isPositive(),
                            review.getId());
        return Optional.of(review);
    }

    @Override
    public void delete(Long id) {
        jdbcTemplate.update("DELETE FROM review_likes WHERE review_id = ?",
                            id);
        jdbcTemplate.update("DELETE FROM reviews WHERE id = ?", id);
    }

    @Override
    public Optional<Review> get(Long id) {
        String sql = "SELECT * FROM reviews WHERE id = ?";
        Review review = null;
        try {
            review = jdbcTemplate.queryForObject(sql, reviewRowMapper, id);
        } catch (EmptyResultDataAccessException e) {
            throw new ReviewNotFoundException(
                    "Review with id %d doesn't exist".formatted(id));
        }
        return Optional.ofNullable(review);
    }

    @Override
    public Collection<Review> getReviewsForFilm(Long filmId,
                                                int countOfReviews
    ) {
        String sql = "SELECT * FROM reviews WHERE film_id = ? ORDER BY useful DESC LIMIT ?";
        return jdbcTemplate.query(sql, reviewRowMapper, filmId,
                                  countOfReviews);
    }

    @Override
    public Collection<Review> getAllReviews(int countOfReviews) {
        String sql = "SELECT * FROM reviews ORDER BY useful DESC LIMIT ?";
        return jdbcTemplate.query(sql, reviewRowMapper, countOfReviews);
    }

    @Override
    public void addLikeToReview(Long reviewId, Long userId) {
        int removedDislike = jdbcTemplate.update(
                "DELETE FROM review_likes WHERE review_id = ? AND user_id = ? AND is_like = FALSE",
                reviewId, userId);
        if (removedDislike > 0) {
            jdbcTemplate.update(
                    "UPDATE reviews SET useful = useful + 1 WHERE id = ?",
                    reviewId);
        }

        jdbcTemplate.update(
                "INSERT INTO review_likes (review_id, user_id, is_like) VALUES (?, ?, TRUE)",
                reviewId, userId);
        jdbcTemplate.update(
                "UPDATE reviews SET useful = useful + 1 WHERE id = ?",
                reviewId);
    }

    @Override
    public void addDislikeToReview(Long reviewId, Long userId) {
        int removedLike = jdbcTemplate.update(
                "DELETE FROM review_likes WHERE review_id = ? AND user_id = ? AND is_like = TRUE",
                reviewId, userId);
        if (removedLike > 0) {
            jdbcTemplate.update(
                    "UPDATE reviews SET useful = useful - 1 WHERE review_id = ?",
                    reviewId);
        }

        jdbcTemplate.update(
                "INSERT INTO review_likes (review_id, user_id, is_like) VALUES (?, ?, FALSE)",
                reviewId, userId);
        jdbcTemplate.update(
                "UPDATE reviews SET useful = useful - 1 WHERE id = ?",
                reviewId);
    }

    @Override
    public void removeLikeFromReview(Long reviewId, Long userId) {
        int removed = jdbcTemplate.update(
                "DELETE FROM review_likes WHERE review_id = ? AND user_id = ? AND is_like = TRUE",
                reviewId, userId);
        if (removed > 0) {
            jdbcTemplate.update(
                    "UPDATE reviews SET useful = useful - 1 WHERE id = ?",
                    reviewId);
        }
    }

    @Override
    public void removeDislikeFromReview(Long reviewId, Long userId) {
        int removed = jdbcTemplate.update(
                "DELETE FROM review_likes WHERE review_id = ? AND user_id = ? AND is_like = FALSE",
                reviewId, userId);
        if (removed > 0) {
            jdbcTemplate.update(
                    "UPDATE reviews SET useful = useful + 1 WHERE id = ?",
                    reviewId);
        }
    }
}
