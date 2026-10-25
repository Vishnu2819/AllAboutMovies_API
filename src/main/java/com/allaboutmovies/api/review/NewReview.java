package com.allaboutmovies.api.review;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record NewReview(
        @Size(max = 40) String author,
        @Min(1) @Max(5) int rating,
        @NotBlank @Size(min = 3, max = 1000) String body
) {
}
