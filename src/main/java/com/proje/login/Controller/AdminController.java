package com.proje.login.Controller;

import com.proje.login.Repository.KullaniciRepo;
import com.proje.login.Repository.WatchlistRepository;
import com.proje.login.Security.CustomUserDetails;
import com.proje.login.Servis.KullaniciAraci;
import com.proje.login.Veri.Kullanici;
import com.proje.login.Veri.Movie;
import com.proje.login.Veri.Watchlist;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ROLE_ADMIN')")
@AllArgsConstructor
public class AdminController {

    private final KullaniciAraci kullaniciAraci;
    private final KullaniciRepo kullaniciRepo;
    private final WatchlistRepository watchlistRepository;
    private final PasswordEncoder passwordEncoder;

    @GetMapping
    public String adminDashboard() {
        return "redirect:/admin/users";
    }

    // User Management
    @GetMapping("/users")
    public String userManagement(Model model) {
        List<Kullanici> kullanicilar = kullaniciAraci.tumKullanicilar();
        model.addAttribute("kullanicilar", kullanicilar);
        return "admin/admin-users";
    }

    @PostMapping("/users/{id}/delete")
    public String deleteUser(@PathVariable int id, RedirectAttributes redirectAttributes) {
        try {
            kullaniciAraci.kullaniciSil(id);
            redirectAttributes.addFlashAttribute("success", "Kullanıcı başarıyla silindi.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Kullanıcı silinirken hata oluştu: " + e.getMessage());
        }
        return "redirect:/admin/users";
    }

    @PostMapping("/users/{id}/update-role")
    public String updateUserRole(@PathVariable int id, @RequestParam String role, 
                               RedirectAttributes redirectAttributes) {
        try {
            Kullanici kullanici = kullaniciAraci.getirKullanici(id);
            if (kullanici != null) {
                kullanici.setRole(role);
                kullaniciAraci.kullaniciGuncelle(kullanici);
                redirectAttributes.addFlashAttribute("success", "Kullanıcı rolü başarıyla güncellendi.");
            } else {
                redirectAttributes.addFlashAttribute("error", "Kullanıcı bulunamadı.");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Rol güncellenirken hata oluştu: " + e.getMessage());
        }
        return "redirect:/admin/users";
    }

    @PostMapping("/users/{id}/reset-password")
    public String resetUserPassword(@PathVariable int id, @RequestParam String newPassword, 
                                 RedirectAttributes redirectAttributes) {
        try {
            Kullanici kullanici = kullaniciAraci.getirKullanici(id);
            if (kullanici != null) {
                kullanici.setPassword(passwordEncoder.encode(newPassword));
                kullaniciAraci.kullaniciGuncelle(kullanici);
                redirectAttributes.addFlashAttribute("success", "Kullanıcı şifresi başarıyla sıfırlandı.");
            } else {
                redirectAttributes.addFlashAttribute("error", "Kullanıcı bulunamadı.");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Şifre sıfırlanırken hata oluştu: " + e.getMessage());
        }
        return "redirect:/admin/users";
    }

    // Watchlist Management
    @GetMapping("/watchlists")
    public String watchlistManagement(Model model) {
        List<Watchlist> watchlists = watchlistRepository.findAll();
        model.addAttribute("watchlists", watchlists);
        return "admin/admin-watchlists";
    }
    
    // Watchlist Detail
    @GetMapping("/watchlists/{id}")
    public String viewWatchlistDetail(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        Optional<Watchlist> watchlistOpt = watchlistRepository.findById(id);
        
        if (watchlistOpt.isPresent()) {
            Watchlist watchlist = watchlistOpt.get();
            
            // Watchlist içindeki filmleri listele
            List<Movie> movies = new ArrayList<>(watchlist.getMovies());
            model.addAttribute("watchlist", watchlist);
            model.addAttribute("movies", movies);
            model.addAttribute("isAdmin", true);
            
            return "watchlist-detail";
        }
        
        redirectAttributes.addFlashAttribute("error", "İzleme listesi bulunamadı.");
        return "redirect:/admin/watchlists";
    }

    @PostMapping("/watchlists/{id}/delete")
    public String deleteWatchlist(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            Optional<Watchlist> watchlistOpt = watchlistRepository.findById(id);
            if (watchlistOpt.isPresent()) {
                watchlistRepository.delete(watchlistOpt.get());
                redirectAttributes.addFlashAttribute("success", "İzleme listesi başarıyla silindi.");
            } else {
                redirectAttributes.addFlashAttribute("error", "İzleme listesi bulunamadı.");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "İzleme listesi silinirken hata oluştu: " + e.getMessage());
        }
        return "redirect:/admin/watchlists";
    }
} 