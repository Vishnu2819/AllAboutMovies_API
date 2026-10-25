package com.allaboutmovies.api.movie;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.util.List;

@Document(collection = "movies")
public record Movie(
        @Id String id,
        @Indexed(unique = true) String imdbId,
        String title,
        LocalDate releaseDate,
        Integer runtimeMinutes,
        List<String> genres,
        String overview,
        String poster,
        List<String> backdrops,
        String trailerLink,
        Integer tmdbId,
        Integer userScore
) {
}
