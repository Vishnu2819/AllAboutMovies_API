package com.allaboutmovies.api.movie;

import com.allaboutmovies.api.review.Review;
import com.allaboutmovies.api.review.ReviewRepository;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Sort;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

class MovieServiceTest {

    final MovieRepository movies = mock(MovieRepository.class);
    final ReviewRepository reviews = mock(ReviewRepository.class);
    final MovieService service = new MovieService(movies, reviews);

    static Movie movie(String id, String title, String... genres) {
        return new Movie(null, id, title, LocalDate.of(2020, 1, 1), 100, List.of(genres), "", "", List.of(), null, null, null);
    }

    @Test
    void filtersByGenreAndTitle() {
        given(movies.findAll(any(Sort.class))).willReturn(List.of(
                movie("tt1", "The Matrix", "Action", "Science Fiction"),
                movie("tt2", "Whiplash", "Drama", "Music"),
                movie("tt3", "Mad Max: Fury Road", "Action")));

        assertThat(service.list("action", null)).extracting(Movie::imdbId).containsExactly("tt1", "tt3");
        assertThat(service.list(null, "  MAT ")).extracting(Movie::imdbId).containsExactly("tt1");
        assertThat(service.list("Drama", "max")).isEmpty();
    }

    @Test
    void averagesRatingsToOneDecimal() {
        given(movies.findByImdbId("tt1")).willReturn(java.util.Optional.of(movie("tt1", "The Matrix")));
        Instant t = Instant.parse("2026-01-01T00:00:00Z");
        given(reviews.findByImdbIdOrderByCreatedAtDesc("tt1")).willReturn(List.of(
                new Review("a", "tt1", "A", 5, "x", t), new Review("b", "tt1", "B", 4, "y", t), new Review("c", "tt1", "C", 4, "z", t)));

        assertThat(service.details("tt1").averageRating()).isEqualTo(4.3);
    }

    @Test
    void noReviewsMeansNoAverage() {
        given(movies.findByImdbId("tt1")).willReturn(java.util.Optional.of(movie("tt1", "The Matrix")));
        given(reviews.findByImdbIdOrderByCreatedAtDesc("tt1")).willReturn(List.of());

        assertThat(service.details("tt1").averageRating()).isNull();
    }

    @Test
    void collectsSortedDistinctGenres() {
        given(movies.findAll()).willReturn(List.of(movie("tt1", "A", "Drama", "Action"), movie("tt2", "B", "Action", "Music")));

        assertThat(service.genres()).containsExactly("Action", "Drama", "Music");
    }
}
