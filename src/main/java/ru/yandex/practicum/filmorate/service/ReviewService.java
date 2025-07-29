package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.logger.LogMethodResult;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.ReviewStorage;

import java.util.Collection;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class ReviewService {
    private final ReviewStorage reviewStorage;
    private final UserService userService;
    private final FilmService filmService;

    @LogMethodResult
    public Review getReviewByIdOrThrow(Long id) {
        return reviewStorage.get(id)
                            .get();
    }

    @LogMethodResult
    public Review add(Review review) {
        review.setUseful(0);
        checkExistenceOfFilmUser(review);
        return reviewStorage.persist(review);
    }

    @LogMethodResult
    public Optional<Review> update(Review review) {
        getReviewByIdOrThrow(review.getId());
        checkExistenceOfFilmUser(review);
        return reviewStorage.update(review);
    }

    @LogMethodResult
    public void delete(Long id) {
        getReviewByIdOrThrow(id);
        reviewStorage.delete(id);
    }

    @LogMethodResult
    public Collection<Review> getReviews(Long filmId, int count) {
        Collection<Review> resReviews;
        if (filmId == null) {
            resReviews = reviewStorage.getAllReviews(count);
        } else {
            resReviews = reviewStorage.getReviewsForFilm(filmId, count);
        }
        return resReviews;
    }

    @LogMethodResult
    public Review addLikeToReview(Long reviewId, Long userId) {
        checkExistenceOfReviewUser(reviewId, userId);
        reviewStorage.addLikeToReview(reviewId, userId);
        return getReviewByIdOrThrow(reviewId);
    }

    @LogMethodResult
    public Review addDislikeToReview(Long reviewId, Long userId) {
        checkExistenceOfReviewUser(reviewId, userId);
        reviewStorage.addLikeToReview(reviewId, userId);
        return getReviewByIdOrThrow(reviewId);
    }

    @LogMethodResult
    public Review removeLikeFromReview(Long reviewId, Long userId) {
        checkExistenceOfReviewUser(reviewId, userId);
        reviewStorage.addLikeToReview(reviewId, userId);
        return getReviewByIdOrThrow(reviewId);
    }

    @LogMethodResult
    public Review removeDislikeFromReview(Long reviewId, Long userId) {
        checkExistenceOfReviewUser(reviewId, userId);
        reviewStorage.addLikeToReview(reviewId, userId);
        return getReviewByIdOrThrow(reviewId);
    }


    private void checkExistenceOfFilmUser(Review review) {
        userService.getUser(review.getUserId());
        filmService.getFilmByIdOrThrow(review.getFilmId());
    }

    private void checkExistenceOfReviewUser(Long reviewId, Long userId) {
        getReviewByIdOrThrow(reviewId);
        userService.getUser(userId);
    }
}
