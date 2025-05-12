package com.proje.login.Controller;

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
} 