package com.proje.login.Controller;

import com.proje.login.DTO.MovieDTO;
import com.proje.login.Repository.MovieRepository;
import com.proje.login.Repository.WatchlistRepository;
import com.proje.login.Security.CustomUserDetails;
import com.proje.login.Servis.TmdbService;
import com.proje.login.Veri.Kullanici;
import com.proje.login.Veri.Movie;
import com.proje.login.Veri.Watchlist;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/watchlists")
public class WatchlistController {

    private final WatchlistRepository watchlistRepository;
    private final MovieRepository movieRepository;
    private final TmdbService tmdbService;

    @Autowired
    public WatchlistController(WatchlistRepository watchlistRepository, 
                              MovieRepository movieRepository,
                              TmdbService tmdbService) {
        this.watchlistRepository = watchlistRepository;
        this.movieRepository = movieRepository;
        this.tmdbService = tmdbService;
    }

    @GetMapping
    public String listWatchlists(
            @AuthenticationPrincipal CustomUserDetails userDetails, //Aktif kullanıcıyı bulur
            @RequestParam(value = "addMovie", required = false) Long movieId,
            @RequestParam(value = "query", required = false) String query,
            @RequestParam(value = "page", defaultValue = "1") Integer page,
            Model model) {
        
        List<Watchlist> watchlists = watchlistRepository.findByUser(userDetails.getKullanici());
        model.addAttribute("watchlists", watchlists);
        model.addAttribute("newWatchlist", new Watchlist());
        
        // Film ID geldi ise, yeni listeye eklemek için 
        if (movieId != null) {
            model.addAttribute("movieId", movieId);
            model.addAttribute("query", query);
            model.addAttribute("page", page);
            model.addAttribute("addToNewWatchlist", true);
        }
        
        return "watchlists";
    }

    @GetMapping("/{id}")
    public String viewWatchlist(@PathVariable Long id, Model model, 
                               @AuthenticationPrincipal CustomUserDetails userDetails) {
        Optional<Watchlist> watchlistOpt = watchlistRepository.findById(id);
        
        if (watchlistOpt.isPresent()) {
            Watchlist watchlist = watchlistOpt.get();
            
            // Kullanıcı yetkisi kontrolü - Admin veya watchlist sahibi ise erişime izin ver
            boolean isAdmin = userDetails.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
            boolean isOwner = watchlist.getUser().getId() == userDetails.getKullanici().getId();
            
            if (!isAdmin && !isOwner) {
                return "redirect:/watchlists?error=unauthorized";
            }
            
            // Watchlist içindeki filmleri listele
            List<Movie> movies = new ArrayList<>(watchlist.getMovies());
            model.addAttribute("watchlist", watchlist);
            model.addAttribute("movies", movies);
            
            return "watchlist-detail";
        }
        
        return "redirect:/watchlists?error=notfound";
    }

    @PostMapping
    public String createWatchlist(@ModelAttribute("newWatchlist") Watchlist watchlist,
                                 @RequestParam(value = "movieId", required = false) Long movieId,
                                 @RequestParam(value = "query", required = false) String query,
                                 @RequestParam(value = "page", defaultValue = "1") Integer page,
                                 @RequestParam(value = "addToNewWatchlist", required = false) Boolean addToNewWatchlist,
                                 @AuthenticationPrincipal CustomUserDetails userDetails,
                                 RedirectAttributes redirectAttributes) {
        Kullanici user = userDetails.getKullanici();
        watchlist.setUser(user);
        
        // Save watchlist
        watchlistRepository.save(watchlist);
        redirectAttributes.addFlashAttribute("success", "İzleme listesi başarıyla oluşturuldu.");
        
        // If there's a movie to add
        if (movieId != null && addToNewWatchlist != null && addToNewWatchlist) {
            // Get or save the movie
            Movie movie = movieRepository.findById(movieId).orElse(null);
            
            if (movie == null) {
                movie = tmdbService.saveMovieToDatabase(movieId);
            }
            
            if (movie != null) {
                watchlist.addMovie(movie);
                watchlistRepository.save(watchlist);
                redirectAttributes.addFlashAttribute("success", "İzleme listesi oluşturuldu ve film eklendi.");
                
                // If coming from search, redirect back
                if (query != null && !query.isEmpty()) {
                    return "redirect:/search/results?query=" + query + "&page=" + page;
                }
            }
        }
        
        return "redirect:/watchlists";
    }

    @PostMapping("/add")
    public String addToWatchlist(@RequestParam("watchlistId") Long watchlistId,
                                @RequestParam("movieId") Long movieId,
                                @RequestParam(value = "query", required = false) String query,
                                @RequestParam(value = "page", defaultValue = "1") Integer page,
                                @AuthenticationPrincipal CustomUserDetails userDetails,
                                RedirectAttributes redirectAttributes) {
        
        // Handle request for a new watchlist creation
        if (watchlistId.equals(Long.valueOf("0")) || "new".equals(watchlistId.toString())) {
            return "redirect:/watchlists?addMovie=" + movieId 
                   + (query != null ? "&query=" + query : "") 
                   + "&page=" + page;
        }
        
        Optional<Watchlist> watchlistOpt = watchlistRepository.findById(watchlistId);
        
        if (watchlistOpt.isPresent()) {
            Watchlist watchlist = watchlistOpt.get();
            
            // Kullanıcı yetkisi kontrolü - sadece watchlist sahibi ekleyebilir
            if (watchlist.getUser().getId() != userDetails.getKullanici().getId()) {
                redirectAttributes.addFlashAttribute("error", "Bu işlem için yetkiniz yok.");
                return "redirect:/watchlists";
            }
            
            // Film veritabanında yoksa API'den bilgileri alıp ekle
            Movie movie = movieRepository.findById(movieId).orElse(null);
            
            if (movie == null) {
                // TMDB API'den film bilgilerini al ve veritabanına ekle
                movie = tmdbService.saveMovieToDatabase(movieId);
            }
            
            if (movie != null) {
                watchlist.addMovie(movie);
                watchlistRepository.save(watchlist);
                redirectAttributes.addFlashAttribute("success", "Film izleme listesine eklendi.");
            } else {
                redirectAttributes.addFlashAttribute("error", "Film eklenirken bir hata oluştu.");
            }
        } else {
            redirectAttributes.addFlashAttribute("error", "İzleme listesi bulunamadı.");
        }
        
        // Arama sayfasından geliyorsa arama sayfasına geri dön
        if (query != null && !query.isEmpty()) {
            return "redirect:/search/results?query=" + query + "&page=" + page;
        }
        
        return "redirect:/watchlists/" + watchlistId;
    }

    @PostMapping("/{watchlistId}/remove/{movieId}")
    public String removeFromWatchlist(@PathVariable Long watchlistId,
                                     @PathVariable Long movieId,
                                     @AuthenticationPrincipal CustomUserDetails userDetails,
                                     RedirectAttributes redirectAttributes) {
        
        Optional<Watchlist> watchlistOpt = watchlistRepository.findById(watchlistId);
        Optional<Movie> movieOpt = movieRepository.findById(movieId);
        
        if (watchlistOpt.isPresent() && movieOpt.isPresent()) {
            Watchlist watchlist = watchlistOpt.get();
            Movie movie = movieOpt.get();
            
            // Kullanıcı yetkisi kontrolü - Admin veya watchlist sahibi ise işleme izin ver
            boolean isAdmin = userDetails.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
            boolean isOwner = watchlist.getUser().getId() == userDetails.getKullanici().getId();
            
            if (!isAdmin && !isOwner) {
                redirectAttributes.addFlashAttribute("error", "Bu işlem için yetkiniz yok.");
                return "redirect:/watchlists";
            }
            
            watchlist.removeMovie(movie);
            watchlistRepository.save(watchlist);
            redirectAttributes.addFlashAttribute("success", "Film izleme listesinden kaldırıldı.");
        } else {
            redirectAttributes.addFlashAttribute("error", "Film veya izleme listesi bulunamadı.");
        }
        
        return "redirect:/watchlists/" + watchlistId;
    }

    @PostMapping("/{id}/delete")
    public String deleteWatchlist(@PathVariable Long id,
                                 @AuthenticationPrincipal CustomUserDetails userDetails,
                                 RedirectAttributes redirectAttributes) {
        
        Optional<Watchlist> watchlistOpt = watchlistRepository.findById(id);
        
        if (watchlistOpt.isPresent()) {
            Watchlist watchlist = watchlistOpt.get();
            
            // Kullanıcı yetkisi kontrolü - Admin veya watchlist sahibi ise işleme izin ver
            boolean isAdmin = userDetails.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
            boolean isOwner = watchlist.getUser().getId() == userDetails.getKullanici().getId();
            
            if (!isAdmin && !isOwner) {
                redirectAttributes.addFlashAttribute("error", "Bu işlem için yetkiniz yok.");
                return "redirect:/watchlists";
            }
            
            watchlistRepository.delete(watchlist);
            redirectAttributes.addFlashAttribute("success", "İzleme listesi başarıyla silindi.");
        } else {
            redirectAttributes.addFlashAttribute("error", "İzleme listesi bulunamadı.");
        }
        
        return "redirect:/watchlists";
    }
} 