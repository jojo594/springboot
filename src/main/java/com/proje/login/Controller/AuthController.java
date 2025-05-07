package com.proje.login.Controller;

import com.proje.login.Servis.AuthService;
import com.proje.login.Veri.Kullanici;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@AllArgsConstructor
@RequestMapping("/auth")
public class AuthController {
    private final AuthService authService;
    private final PasswordEncoder passwordEncoder;

    @GetMapping("/login")
    public String loginPage() {
        // Redirect to home if user is already authenticated
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()
                && !(authentication instanceof AnonymousAuthenticationToken)) {
            return "redirect:/home";
        }
        return "login";
    }

    @GetMapping("/register")
    public String registerPage() {
        // Redirect to home if user is already authenticated
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()
                && !(authentication instanceof AnonymousAuthenticationToken)) {
            return "redirect:/home";
        }
        return "register";
    }

    @PostMapping("/register")
    public String register(@RequestParam String username,
                           @RequestParam String email,
                           @RequestParam String password,
                           @RequestParam int yas,
                           RedirectAttributes redirectAttributes) {
        // Encode password before saving
        Kullanici kullanici = new Kullanici(email, passwordEncoder.encode(password), username, yas);
        Kullanici kayit = authService.register(kullanici);

        if (kayit != null) {
            redirectAttributes.addFlashAttribute("success", "Kayıt işlemi başarılı! Lütfen giriş yapın.");
            return "redirect:/auth/login";
        }

        redirectAttributes.addFlashAttribute("error", "Kayıt işlemi başarısız! Bu email zaten kullanılıyor olabilir.");
        return "redirect:/auth/register";
    }

    // Spring Security will handle the login processing
    // This is just for showing login errors
    @GetMapping("/login-error")
    public String loginError(Model model) {
        model.addAttribute("error", "Geçersiz e-posta veya şifre!");
        return "login";
    }
}