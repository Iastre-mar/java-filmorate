package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.service.ReviewService;

import java.util.Collection;

@RequiredArgsConstructor
@RestController
@RequestMapping("/reviews")
public class ReviewController {
    ReviewService reviewService;

    @GetMapping("{id}")
    public Review getReviewById(@PathVariable Long id) {
        return reviewService.getReviewByIdOrThrow(id);
    }

    @PostMapping
    public Review createReview(@RequestBody Review review) {
        return reviewService.add(review);
    }

    @PutMapping
    public Review updateReview(@RequestBody Review review) {
        return reviewService.update(review)
                            .get();
    }

    @GetMapping
    public Collection<Review> getReviews(
            @RequestParam(required = false) Long filmId,
            @RequestParam(defaultValue = "10") Integer count
    ) {
        return reviewService.getReviews(filmId, count);
    }

    @DeleteMapping("{id}")
    public void deleteReview(@PathVariable Long id) {
        reviewService.delete(id);
    }

    @PutMapping("{id}/like/{userId}")
    public Review addLikeToReview(@PathVariable Long id,
                                  @PathVariable Long userId
    ) {
        return reviewService.addLikeToReview(id, userId);
    }

    @PutMapping("{id}/dislike/{userId}")
    public Review addDislikeToReview(@PathVariable Long id,
                                     @PathVariable Long userId
    ) {
        return reviewService.addDislikeToReview(id, userId);
    }

    @DeleteMapping("{id}/like/{userId}")
    public Review removeLikeFromReview(@PathVariable Long id,
                                       @PathVariable Long userId
    ) {
        return reviewService.removeLikeFromReview(id, userId);
    }


    @DeleteMapping("{id}/dislike/{userId}")
    public Review removeDislikeToReview(@PathVariable Long id,
                                        @PathVariable Long userId
    ) {
        return reviewService.removeDislikeFromReview(id, userId);
    }
}
