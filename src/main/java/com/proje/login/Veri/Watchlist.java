package com.proje.login.Veri;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Setter @Getter
@AllArgsConstructor @NoArgsConstructor
@Entity
@Table(name = "watchlists")
public class Watchlist {

    // Getters and Setters
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String name;
    
    private String description;
    
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private Kullanici user;
    
    @ManyToMany
    @JoinTable(
        name = "watchlist_movies",
        joinColumns = @JoinColumn(name = "watchlist_id"),
        inverseJoinColumns = @JoinColumn(name = "movie_id")
    )
    private Set<Movie> movies = new HashSet<>();

    // Constructor with fields
    public Watchlist(String name, String description, Kullanici user) {
        this.name = name;
        this.description = description;
        this.user = user;
    }

    // Helper methods
    public void addMovie(Movie movie) {
        this.movies.add(movie);
    }
    
    public void removeMovie(Movie movie) {
        this.movies.remove(movie);
    }
} 