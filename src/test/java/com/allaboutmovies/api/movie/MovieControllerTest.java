package com.allaboutmovies.api.movie;

import com.allaboutmovies.api.error.NotFoundException;
import com.allaboutmovies.api.review.Review;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MovieController.class)
class MovieControllerTest {

    static final Movie INCEPTION = new Movie("1", "tt1375666", "Inception", LocalDate.of(2010, 7, 16), 148,
            List.of("Action", "Science Fiction"), "A thief who steals secrets...", "p.jpg", List.of("b.jpg"),
            "https://www.youtube.com/watch?v=Jvurpf91omw", 27205, 84);

    @Autowired MockMvc mvc;
    @MockBean MovieService service;

    @Test
    void listsMovies() throws Exception {
        given(service.list(any(), any())).willReturn(List.of(INCEPTION));

        mvc.perform(get("/api/v1/movies"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].imdbId").value("tt1375666"))
                .andExpect(jsonPath("$[0].releaseDate").value("2010-07-16"));
    }

    @Test
    void returnsDetailsWithReviews() throws Exception {
        Review r = new Review("r1", "tt1375666", "Sam", 5, "Mind-bending.", Instant.parse("2026-10-01T00:00:00Z"));
        given(service.details("tt1375666")).willReturn(new MovieDetails(INCEPTION, List.of(r), 5.0));

        mvc.perform(get("/api/v1/movies/tt1375666"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.movie.title").value("Inception"))
                .andExpect(jsonPath("$.reviews[0].author").value("Sam"))
                .andExpect(jsonPath("$.averageRating").value(5.0));
    }

    @Test
    void unknownMovieIsProblem404() throws Exception {
        given(service.details("tt0")).willThrow(new NotFoundException("No movie with imdbId tt0"));

        mvc.perform(get("/api/v1/movies/tt0"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.detail").value("No movie with imdbId tt0"));
    }

    @Test
    void allowsConfiguredCorsOrigin() throws Exception {
        given(service.list(any(), any())).willReturn(List.of());

        mvc.perform(get("/api/v1/movies").header("Origin", "https://vishnu2819.github.io"))
                .andExpect(status().isOk())
                .andExpect(header().string("Access-Control-Allow-Origin", "https://vishnu2819.github.io"));
    }
}
