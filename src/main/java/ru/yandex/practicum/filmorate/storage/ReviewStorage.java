package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Review;

import java.util.Collection;
import java.util.Optional;

public interface ReviewStorage {
    Review persist(Review review);

    Optional<Review> update(Review review);

    void delete(Long id);

    Optional<Review> get(Long id);

    Collection<Review> getReviewsForFilm(Long filmId, int countOfReviews);

    Collection<Review> getAllReviews(int countOfReviews);

    void addLikeToReview(Long reviewId, Long userId);

    void addDislikeToReview(Long reviewId, Long userId);

    void removeLikeFromReview(Long reviewId, Long userId);

    void removeDislikeFromReview(Long reviewId, Long userId);
}
