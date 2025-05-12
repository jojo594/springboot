package com.proje.login.Controller;

import com.proje.login.DTO.KullaniciDTO;
import com.proje.login.Security.CustomUserDetails;
import com.proje.login.Servis.KullaniciAraci;
import com.proje.login.Veri.Kullanici;
import lombok.AllArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@AllArgsConstructor
public class ProfileController {

    private final KullaniciAraci kullaniciAraci;
    private final PasswordEncoder passwordEncoder;

    @GetMapping("/profile")
    public String profilePage(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
        // Get the logged-in user's information
        Kullanici kullanici = kullaniciAraci.getirKullanici(userDetails.getKullanici().getId());
        model.addAttribute("kullanici", kullanici);
        return "profile";
    }

    @PostMapping("/profile/update")
    public String updateProfile(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) Integer yas,
            @RequestParam(required = false) String password,
            @RequestParam(required = false) String confirmPassword,
            RedirectAttributes redirectAttributes) {

        // Get the current user
        Kullanici kullanici = kullaniciAraci.getirKullanici(userDetails.getKullanici().getId());
        
        // Update fields if provided
        boolean hasChanges = false;
        
        if (username != null && !username.trim().isEmpty() && !username.equals(kullanici.getUsername())) {
            kullanici.setUsername(username);
            hasChanges = true;
        }
        
        if (email != null && !email.trim().isEmpty() && !email.equals(kullanici.getEmail())) {
            kullanici.setEmail(email);
            hasChanges = true;
        }
        
        if (yas != null && yas > 0 && yas != kullanici.getYas()) {
            kullanici.setYas(yas);
            hasChanges = true;
        }
        
        // Handle password change if provided
        if (password != null && !password.trim().isEmpty()) {
            // Verify that confirmation matches
            if (confirmPassword == null || !password.equals(confirmPassword)) {
                redirectAttributes.addFlashAttribute("error", "Şifreler eşleşmiyor!");
                return "redirect:/profile";
            }
            
            // Encode and set the new password
            kullanici.setPassword(passwordEncoder.encode(password));
            hasChanges = true;
        }
        
        // Save changes if any were made
        if (hasChanges) {
            kullaniciAraci.kullaniciGuncelle(kullanici);
            redirectAttributes.addFlashAttribute("success", "Profil bilgileriniz başarıyla güncellendi.");
        } else {
            redirectAttributes.addFlashAttribute("info", "Herhangi bir değişiklik yapılmadı.");
        }
        
        return "redirect:/profile";
    }
} 