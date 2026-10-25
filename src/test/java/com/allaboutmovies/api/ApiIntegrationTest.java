package com.allaboutmovies.api;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/** Full stack against an embedded MongoDB: seeding, queries and the review round-trip. */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("it")
class ApiIntegrationTest {

    @Autowired MockMvc mvc;

    @Test
    void seedsMoviesAndRoundTripsAReview() throws Exception {
        mvc.perform(get("/api/v1/movies"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(14))
                .andExpect(jsonPath("$[0].title").value("Dune: Part Two"));

        mvc.perform(get("/api/v1/movies").param("genre", "Animation"))
                .andExpect(jsonPath("$.length()").value(2));

        mvc.perform(get("/api/v1/genres")).andExpect(jsonPath("$", hasItem("Science Fiction")));

        mvc.perform(post("/api/v1/movies/tt0133093/reviews").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"author\":\"Neo\",\"rating\":5,\"body\":\"There is no spoon.\"}"))
                .andExpect(status().isCreated());
        mvc.perform(post("/api/v1/movies/tt0133093/reviews").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"rating\":4,\"body\":\"Still holds up.\"}"))
                .andExpect(status().isCreated());

        mvc.perform(get("/api/v1/movies/tt0133093"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.movie.title").value("The Matrix"))
                .andExpect(jsonPath("$.reviews.length()", greaterThanOrEqualTo(2)))
                .andExpect(jsonPath("$.reviews[0].author").value("Anonymous"))
                .andExpect(jsonPath("$.averageRating").value(4.5));

        mvc.perform(get("/api/v1/movies/tt0000000")).andExpect(status().isNotFound());
        mvc.perform(get("/actuator/health")).andExpect(jsonPath("$.status").value("UP"));
    }
}
