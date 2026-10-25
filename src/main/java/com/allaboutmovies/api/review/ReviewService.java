package com.allaboutmovies.api.review;

import com.allaboutmovies.api.error.NotFoundException;
import com.allaboutmovies.api.movie.MovieRepository;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.Instant;
import java.util.List;

@Service
public class ReviewService {

    private final ReviewRepository reviews;
    private final MovieRepository movies;
    private final Clock clock;

    public ReviewService(ReviewRepository reviews, MovieRepository movies, Clock clock) {
        this.reviews = reviews;
        this.movies = movies;
        this.clock = clock;
    }

    public List<Review> forMovie(String imdbId) {
        requireMovie(imdbId);
        return reviews.findByImdbIdOrderByCreatedAtDesc(imdbId);
    }

    public Review create(String imdbId, NewReview input) {
        requireMovie(imdbId);
        String author = input.author() == null || input.author().isBlank() ? "Anonymous" : input.author().trim();
        return reviews.save(new Review(null, imdbId, author, input.rating(), input.body().trim(), Instant.now(clock)));
    }

    private void requireMovie(String imdbId) {
        if (!movies.existsByImdbId(imdbId)) {
            throw new NotFoundException("No movie with imdbId " + imdbId);
        }
    }
}
