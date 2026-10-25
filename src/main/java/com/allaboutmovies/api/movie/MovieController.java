package com.allaboutmovies.api.movie;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class MovieController {

    private final MovieService service;

    public MovieController(MovieService service) {
        this.service = service;
    }

    @GetMapping("/movies")
    public List<Movie> list(@RequestParam(required = false) String genre,
                            @RequestParam(name = "q", required = false) String query) {
        return service.list(genre, query);
    }

    @GetMapping("/movies/{imdbId}")
    public MovieDetails get(@PathVariable String imdbId) {
        return service.details(imdbId);
    }

    @GetMapping("/genres")
    public List<String> genres() {
        return service.genres();
    }
}
