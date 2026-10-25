package com.allaboutmovies.api.movie;

import com.allaboutmovies.api.error.NotFoundException;
import com.allaboutmovies.api.review.Review;
import com.allaboutmovies.api.review.ReviewRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.TreeSet;

@Service
public class MovieService {

    private final MovieRepository movies;
    private final ReviewRepository reviews;

    public MovieService(MovieRepository movies, ReviewRepository reviews) {
        this.movies = movies;
        this.reviews = reviews;
    }

    /** All movies, newest first, optionally filtered by genre and a case-insensitive title search. */
    public List<Movie> list(String genre, String query) {
        String q = query == null ? "" : query.trim().toLowerCase(Locale.ROOT);
        return movies.findAll(Sort.by(Sort.Direction.DESC, "releaseDate")).stream()
                .filter(m -> genre == null || genre.isBlank() || m.genres().stream().anyMatch(g -> g.equalsIgnoreCase(genre)))
                .filter(m -> q.isEmpty() || m.title().toLowerCase(Locale.ROOT).contains(q))
                .toList();
    }

    public Movie get(String imdbId) {
        return movies.findByImdbId(imdbId)
                .orElseThrow(() -> new NotFoundException("No movie with imdbId " + imdbId));
    }

    public MovieDetails details(String imdbId) {
        Movie movie = get(imdbId);
        List<Review> list = reviews.findByImdbIdOrderByCreatedAtDesc(imdbId);
        Double avg = list.isEmpty() ? null
                : Math.round(list.stream().mapToInt(Review::rating).average().orElse(0) * 10) / 10.0;
        return new MovieDetails(movie, list, avg);
    }

    public List<String> genres() {
        TreeSet<String> all = new TreeSet<>(Comparator.naturalOrder());
        movies.findAll().forEach(m -> all.addAll(m.genres()));
        return List.copyOf(all);
    }
}
