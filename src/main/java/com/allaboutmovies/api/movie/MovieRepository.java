package com.allaboutmovies.api.movie;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface MovieRepository extends MongoRepository<Movie, String> {

    Optional<Movie> findByImdbId(String imdbId);

    boolean existsByImdbId(String imdbId);
}
