package com.proje.login.Controller;

import com.proje.login.DTO.AuthDTO;
import com.proje.login.Servis.AuthService;
import com.proje.login.Veri.Kullanici;
import jakarta.servlet.http.HttpSession;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@AllArgsConstructor
@RequestMapping("/auth")
public class AuthController {
    private final AuthService authService;

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    @GetMapping("/register")
    public String registerPage() {
        return "register";
    }

    @PostMapping("/register")
    public String register(@RequestParam String username,
                          @RequestParam String email,
                          @RequestParam String password,
                          @RequestParam int yas,
                          RedirectAttributes redirectAttributes) {
        Kullanici kullanici = new Kullanici(email, password, username, yas);
        Kullanici kayit = authService.register(kullanici);
        
        if (kayit != null) {
            return "redirect:/auth/login";
        }
        
        redirectAttributes.addFlashAttribute("error", "Kayıt işlemi başarısız!");
        return "redirect:/auth/register";
    }

    @PostMapping("/login")
    public String login(@RequestParam String email, 
                       @RequestParam String password,
                       HttpSession session,
                       RedirectAttributes redirectAttributes) {
        AuthDTO authDTO = new AuthDTO(email, password);
        Kullanici kullanici = authService.login(authDTO);
        
        if (kullanici != null) {
            return "redirect:/home";
        }
        
        redirectAttributes.addFlashAttribute("error", "Geçersiz e-posta veya şifre!");
        return "redirect:/auth/login";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        authService.logout();
        return "redirect:/auth/login";
    }
} 