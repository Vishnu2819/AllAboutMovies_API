package com.allaboutmovies.api.config;

import com.allaboutmovies.api.movie.Movie;
import com.allaboutmovies.api.movie.MovieRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/** Loads movies.json into an empty database on startup so the API is usable out of the box. */
@Component
@ConditionalOnProperty(name = "app.seed.enabled", havingValue = "true", matchIfMissing = true)
public class MovieSeeder implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(MovieSeeder.class);

    private final MovieRepository movies;
    private final ObjectMapper mapper;

    public MovieSeeder(MovieRepository movies, ObjectMapper mapper) {
        this.movies = movies;
        this.mapper = mapper;
    }

    @Override
    public void run(ApplicationArguments args) throws IOException {
        if (movies.count() > 0) {
            return;
        }
        try (InputStream in = new ClassPathResource("movies.json").getInputStream()) {
            List<Movie> seed = mapper.readValue(in, new TypeReference<>() { });
            movies.saveAll(seed);
            log.info("Seeded {} movies", seed.size());
        }
    }
}
