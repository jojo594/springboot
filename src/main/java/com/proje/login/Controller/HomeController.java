package com.proje.login.Controller;

import com.proje.login.Servis.AuthService;
import com.proje.login.Veri.Kullanici;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@AllArgsConstructor
public class HomeController {

    private final AuthService authService;

    @GetMapping("/home")
    public String home(Model model) {
        // Get current user information to display in the home page
        Kullanici currentUser = authService.getCurrentUser();
        model.addAttribute("kullanici", currentUser); // Add current user to the model, used later
        return "home";
    }

    @GetMapping("/")
    public String index() {
        return "redirect:/home";
    }
}