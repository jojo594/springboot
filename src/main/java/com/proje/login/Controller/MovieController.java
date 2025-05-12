package com.proje.login.Controller;

import com.proje.login.DTO.GenreListDTO;
import com.proje.login.DTO.MovieSearchResultDTO;
import com.proje.login.Repository.WatchlistRepository;
import com.proje.login.Security.CustomUserDetails;
import com.proje.login.Servis.TmdbService;
import com.proje.login.Veri.Watchlist;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.Year;
import java.util.ArrayList;
import java.util.List;

@Controller
public class MovieController {

    private final TmdbService tmdbService;
    private final WatchlistRepository watchlistRepository;
    
    @Autowired
    public MovieController(TmdbService tmdbService, WatchlistRepository watchlistRepository) {
        this.tmdbService = tmdbService;
        this.watchlistRepository = watchlistRepository;
    }
    
    @GetMapping("/search")
    public String searchPage() {
        return "search";
    }
    
    @GetMapping("/search/results")
    public String searchMovies(
            @RequestParam(value = "query", required = false) String query,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            Model model) {
        
        if (query != null && !query.isEmpty()) {
            MovieSearchResultDTO results = tmdbService.searchMovies(query, page);
            model.addAttribute("results", results);
            model.addAttribute("query", query);
            model.addAttribute("currentPage", page);
            
            // Giriş yapmış kullanıcı için izleme listelerini modele ekle
            if (userDetails != null) {
                List<Watchlist> watchlists = watchlistRepository.findByUser(userDetails.getKullanici());
                model.addAttribute("watchlists", watchlists);
            }
        }
        
        return "search-results";
    }
    
    @GetMapping("/films")
    public String showFilms(
            @RequestParam(value = "year", required = false) Integer year,
            @RequestParam(value = "genre", required = false) String genre,
            @RequestParam(value = "sort_by", required = false, defaultValue = "popularity.desc") String sortBy,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            Model model) {
        
        // Tüm film türlerini al
        GenreListDTO genreList = tmdbService.getMovieGenres();
        model.addAttribute("genres", genreList.getGenres());
        
        // Yıl listesi
        List<Integer> years = new ArrayList<>();
        int currentYear = Year.now().getValue();
        for (int i = currentYear; i >= 1980; i--) {
            years.add(i);
        }
        model.addAttribute("years", years);
        
        // Sıralama seçenekleri
        model.addAttribute("sortOptions", getSortOptions());
        
        // Filmleri filtrele
        MovieSearchResultDTO results = tmdbService.discoverMovies(year, genre, sortBy, page);
        
        model.addAttribute("results", results);
        model.addAttribute("selectedYear", year);
        model.addAttribute("selectedGenre", genre);
        model.addAttribute("selectedSort", sortBy);
        model.addAttribute("currentPage", page);
        
        // Giriş yapmış kullanıcı için izleme listelerini modele ekle
        if (userDetails != null) {
            List<Watchlist> watchlists = watchlistRepository.findByUser(userDetails.getKullanici());
            model.addAttribute("watchlists", watchlists);
        }
        
        return "films";
    }
    
    private List<SortOption> getSortOptions() {
        List<SortOption> options = new ArrayList<>();
        options.add(new SortOption("popularity.desc", "Popülerliğe Göre (Yüksek → Düşük)"));
        options.add(new SortOption("popularity.asc", "Popülerliğe Göre (Düşük → Yüksek)"));
        options.add(new SortOption("vote_average.desc", "Puanlamaya Göre (Yüksek → Düşük)"));
        options.add(new SortOption("vote_average.asc", "Puanlamaya Göre (Düşük → Yüksek)"));
        options.add(new SortOption("release_date.desc", "Yayın Tarihine Göre (Yeni → Eski)"));
        options.add(new SortOption("release_date.asc", "Yayın Tarihine Göre (Eski → Yeni)"));
        options.add(new SortOption("revenue.desc", "Hasılata Göre (Yüksek → Düşük)"));
        return options;
    }
    
    // Sıralama seçeneği için iç sınıf
    public static class SortOption {
        private final String value;
        private final String label;
        
        public SortOption(String value, String label) {
            this.value = value;
            this.label = label;
        }
        
        public String getValue() {
            return value;
        }
        
        public String getLabel() {
            return label;
        }
    }
} 