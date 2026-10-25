package com.allaboutmovies.api.review;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document(collection = "reviews")
public record Review(
        @Id String id,
        @Indexed String imdbId,
        String author,
        int rating,
        String body,
        Instant createdAt
) {
}
