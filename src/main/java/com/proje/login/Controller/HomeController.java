package com.proje.login.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {
    
    @GetMapping("/home")
    public String home() {
        return "home";
    }
    
    @GetMapping("/")
    public String index() {
        return "redirect:/home";
    }
} 