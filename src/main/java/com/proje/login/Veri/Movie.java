package com.proje.login.Veri;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Entity @AllArgsConstructor @NoArgsConstructor
@Getter @Setter
@Table(name = "movies")
public class Movie {
    
    @Id
    private Long tmdbId;
    
    @Column(nullable = false)
    private String title;
    
    @Column(columnDefinition = "TEXT")
    private String overview;
    
    private String posterPath;
    
    private String backdropPath;
    
    @Column(name = "release_date")
    private Date releaseDate;
    
    private Double voteAverage;
    
    private Integer voteCount;
}
    