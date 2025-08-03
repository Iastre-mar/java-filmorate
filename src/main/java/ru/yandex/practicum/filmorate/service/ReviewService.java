package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.ReviewNotFoundException;
import ru.yandex.practicum.filmorate.logger.LogMethodResult;
import ru.yandex.practicum.filmorate.model.Event;
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
    private final EventService eventService;

    @LogMethodResult
    public Review getReviewByIdOrThrow(Long id) {
        return reviewStorage.get(id)
                .orElseThrow(() -> new ReviewNotFoundException("Review not found with id: " + id));
    }

    @LogMethodResult
    public Review add(Review review) {
        review.setUseful(0L);
        checkExistenceOfFilmUser(review);
        Review savedReview = reviewStorage.persist(review);

        addReviewEvent(savedReview.getUserId(), savedReview.getReviewId(), Event.Operation.ADD);

        return savedReview;
    }

    @LogMethodResult
    public Optional<Review> update(Review review) {
        Review existingReview = getReviewByIdOrThrow(review.getReviewId());
        checkExistenceOfFilmUser(review);
        Optional<Review> updatedReview = reviewStorage.update(review);

        if (updatedReview.isPresent()) {
            addReviewEvent(existingReview.getUserId(), existingReview.getReviewId(), Event.Operation.UPDATE);
        }

        return updatedReview;
    }

    @LogMethodResult
    public void delete(Long id) {
        Review review = getReviewByIdOrThrow(id);

        addReviewEvent(review.getUserId(), review.getReviewId(), Event.Operation.REMOVE);

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
        reviewStorage.addDislikeToReview(reviewId, userId);
        return getReviewByIdOrThrow(reviewId);
    }

    @LogMethodResult
    public Review removeLikeFromReview(Long reviewId, Long userId) {
        checkExistenceOfReviewUser(reviewId, userId);
        reviewStorage.removeLikeFromReview(reviewId, userId);
        return getReviewByIdOrThrow(reviewId);
    }

    @LogMethodResult
    public Review removeDislikeFromReview(Long reviewId, Long userId) {
        checkExistenceOfReviewUser(reviewId, userId);
        reviewStorage.removeDislikeFromReview(reviewId, userId);
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

    private void addReviewEvent(Long userId, Long reviewId, Event.Operation operation) {
        Event event = new Event();
        event.setUserId(userId);
        event.setEntityId(reviewId);
        event.setEventType(Event.EventType.REVIEW);
        event.setOperation(operation);
        event.setTimestamp(System.currentTimeMillis());
        eventService.addEvent(event);
    }
}