package com.allaboutmovies.api.review;

import com.allaboutmovies.api.error.NotFoundException;
import com.allaboutmovies.api.movie.MovieRepository;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

class ReviewServiceTest {

    final ReviewRepository reviews = mock(ReviewRepository.class);
    final MovieRepository movies = mock(MovieRepository.class);
    final Instant now = Instant.parse("2026-10-24T12:00:00Z");
    final ReviewService service = new ReviewService(reviews, movies, Clock.fixed(now, ZoneOffset.UTC));

    @Test
    void savesTrimmedReviewWithTimestampAndDefaultAuthor() {
        given(movies.existsByImdbId("tt1")).willReturn(true);
        given(reviews.save(any())).willAnswer(inv -> inv.getArgument(0));

        Review saved = service.create("tt1", new NewReview("  ", 4, "  Great pacing.  "));

        assertThat(saved.author()).isEqualTo("Anonymous");
        assertThat(saved.body()).isEqualTo("Great pacing.");
        assertThat(saved.createdAt()).isEqualTo(now);
        assertThat(saved.imdbId()).isEqualTo("tt1");
    }

    @Test
    void refusesReviewsForUnknownMovies() {
        given(movies.existsByImdbId("tt0")).willReturn(false);

        assertThatThrownBy(() -> service.create("tt0", new NewReview("A", 3, "text")))
                .isInstanceOf(NotFoundException.class);
        verify(reviews, never()).save(any());
    }
}
