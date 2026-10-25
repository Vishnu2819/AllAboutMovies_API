package com.allaboutmovies.api.review;

import com.allaboutmovies.api.error.NotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ReviewController.class)
class ReviewControllerTest {

    @Autowired MockMvc mvc;
    @MockBean ReviewService service;

    @Test
    void createsReview() throws Exception {
        given(service.create(eq("tt1375666"), any())).willReturn(
                new Review("r1", "tt1375666", "Sam", 5, "Mind-bending.", Instant.parse("2026-10-01T00:00:00Z")));

        mvc.perform(post("/api/v1/movies/tt1375666/reviews").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"author\":\"Sam\",\"rating\":5,\"body\":\"Mind-bending.\"}"))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "http://localhost/api/v1/movies/tt1375666/reviews/r1"))
                .andExpect(jsonPath("$.id").value("r1"));
    }

    @Test
    void rejectsInvalidReview() throws Exception {
        mvc.perform(post("/api/v1/movies/tt1375666/reviews").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"rating\":9,\"body\":\"\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.rating").exists())
                .andExpect(jsonPath("$.errors.body").exists());
    }

    @Test
    void reviewForUnknownMovieIs404() throws Exception {
        given(service.create(eq("tt0"), any())).willThrow(new NotFoundException("No movie with imdbId tt0"));

        mvc.perform(post("/api/v1/movies/tt0/reviews").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"rating\":3,\"body\":\"Fine film\"}"))
                .andExpect(status().isNotFound());
    }
}
