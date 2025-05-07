package com.proje.login.Servis;

import com.proje.login.DTO.AuthDTO;
import com.proje.login.Repository.KullaniciRepo;
import com.proje.login.Veri.Kullanici;
import jakarta.servlet.http.HttpSession;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@AllArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final KullaniciRepo kullaniciRepo;
    private final HttpSession session;

    @Override
    public Kullanici login(AuthDTO authDTO) {
        Kullanici kullanici = kullaniciRepo.findByEmail(authDTO.getEmail());
        
        if (kullanici != null && kullanici.getPassword().equals(authDTO.getPassword())) {
            kullanici.setLastLoginDate(LocalDateTime.now());
            kullaniciRepo.save(kullanici);
            session.setAttribute("user", kullanici);
            return kullanici;
        }
        return null;
    }

    @Override
    public void logout() {
        session.invalidate();
    }

    @Override
    public Kullanici getCurrentUser() {
        return (Kullanici) session.getAttribute("user");
    }

    @Override
    public Kullanici register(Kullanici kullanici) {
        if (kullaniciRepo.findByEmail(kullanici.getEmail()) != null) {
            return null; // Email already exists
        }
        return kullaniciRepo.save(kullanici);
    }
} 