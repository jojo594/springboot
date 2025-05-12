package com.proje.login.Servis;

import com.proje.login.DTO.MovieDTO;
import com.proje.login.DTO.MovieSearchResultDTO;
import com.proje.login.Repository.MovieRepository;
import com.proje.login.Veri.Movie;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;

@Service
public class TmdbService {

    private final RestTemplate restTemplate;
    private final String apiKey;
    private final String baseUrl = "https://api.themoviedb.org/3";
    private final MovieRepository movieRepository;
    
    @Autowired
    public TmdbService(
            @Value("${tmdb.api.key}") String apiKey,
            MovieRepository movieRepository) {
        this.restTemplate = new RestTemplate();
        this.apiKey = apiKey;
        this.movieRepository = movieRepository;
    }
    
    public MovieSearchResultDTO searchMovies(String query, int page) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("accept", "application/json");
        headers.set("Authorization", "Bearer " + apiKey);
        
        HttpEntity<String> entity = new HttpEntity<>(headers);
        
        String url = baseUrl + "/search/movie?include_adult=false&language=tr-TR&page=" + page + "&query=" + query;
        
        ResponseEntity<MovieSearchResultDTO> response = restTemplate.exchange(
                url, 
                HttpMethod.GET, 
                entity, 
                MovieSearchResultDTO.class);
        
        return response.getBody();
    }
    
    public MovieDTO getMovieDetails(Long movieId) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("accept", "application/json");
        headers.set("Authorization", "Bearer " + apiKey);
        
        HttpEntity<String> entity = new HttpEntity<>(headers);
        
        String url = baseUrl + "/movie/" + movieId + "?language=tr-TR";
        
        ResponseEntity<MovieDTO> response = restTemplate.exchange(
                url, 
                HttpMethod.GET, 
                entity, 
                MovieDTO.class);
        
        return response.getBody();
    }
    
    public Movie saveMovieToDatabase(Long movieId) {
        // Önce veritabanında kontrol et
        Optional<Movie> existingMovie = movieRepository.findById(movieId);
        if (existingMovie.isPresent()) {
            return existingMovie.get();
        }
        
        // API'den film detaylarını al
        MovieDTO movieDTO = getMovieDetails(movieId);
        if (movieDTO == null) {
            return null;
        }
        
        // DTO'dan Entity'ye dönüştür
        Movie movie = new Movie();
        movie.setTmdbId(movieDTO.getId());
        movie.setTitle(movieDTO.getTitle());
        movie.setOverview(movieDTO.getOverview());
        movie.setPosterPath(movieDTO.getPosterPath());
        movie.setBackdropPath(movieDTO.getBackdropPath());
        movie.setVoteAverage(movieDTO.getVoteAverage());
        movie.setVoteCount(movieDTO.getVoteCount());
        
        // Release date'i Date formatına çevir
        if (movieDTO.getReleaseDate() != null && !movieDTO.getReleaseDate().isEmpty()) {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            try {
                Date releaseDate = format.parse(movieDTO.getReleaseDate());
                movie.setReleaseDate(releaseDate);
            } catch (ParseException e) {
                // Tarih formatı hatası, boş bırak
            }
        }
        
        // Veritabanına kaydet
        return movieRepository.save(movie);
    }
} 