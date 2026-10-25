package com.allaboutmovies.api.movie;

import com.allaboutmovies.api.review.Review;

import java.util.List;

/** A movie together with its reviews and their average rating. */
public record MovieDetails(Movie movie, List<Review> reviews, Double averageRating) {
}
