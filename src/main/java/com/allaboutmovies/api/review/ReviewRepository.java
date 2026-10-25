package com.allaboutmovies.api.review;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ReviewRepository extends MongoRepository<Review, String> {

    List<Review> findByImdbIdOrderByCreatedAtDesc(String imdbId);
}
