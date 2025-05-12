package com.proje.login.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Setter @Getter
@NoArgsConstructor @AllArgsConstructor
public class MovieDTO {

    // Getters and Setters
    private Long id;
    private String title;
    private String overview;
    
    @JsonProperty("poster_path")
    private String posterPath;
    
    @JsonProperty("backdrop_path")
    private String backdropPath;
    
    @JsonProperty("release_date")
    private String releaseDate;
    
    @JsonProperty("vote_average")
    private Double voteAverage;
    
    @JsonProperty("vote_count")
    private Integer voteCount;

    // Helper method to get full poster URL
    public String getFullPosterPath() {
        if (posterPath == null) {
            return null;
        }
        return "https://image.tmdb.org/t/p/w500" + posterPath;
    }
    
    // Helper method to get full backdrop URL
    public String getFullBackdropPath() {
        if (backdropPath == null) {
            return null;
        }
        return "https://image.tmdb.org/t/p/original" + backdropPath;
    }
} 