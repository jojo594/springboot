package com.proje.login.Servis;

import com.proje.login.DTO.AuthDTO;
import com.proje.login.Repository.KullaniciRepo;
import com.proje.login.Veri.Kullanici;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@AllArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final KullaniciRepo kullaniciRepo;

    @Override
    public Kullanici login(AuthDTO authDTO) {
        // This method is now only used for non-Spring Security login
        // (if you want to keep it as a fallback)
        return null;
    }

    @Override
    public void logout() {
        // This is now handled by Spring Security
        SecurityContextHolder.clearContext();
    }

    @Override
    public Kullanici getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() &&
                !(authentication instanceof org.springframework.security.authentication.AnonymousAuthenticationToken)) {
            return kullaniciRepo.findByEmail(authentication.getName());
        }
        return null;
    }

    @Override
    public Kullanici register(Kullanici kullanici) {
        if (kullaniciRepo.findByEmail(kullanici.getEmail()) != null) {
            return null; // Email already exists
        }

        // Set registration date
        kullanici.setLastLoginDate(LocalDateTime.now());

        return kullaniciRepo.save(kullanici);
    }
}