package com.proje.login.Security;

import com.proje.login.Repository.KullaniciRepo;
import com.proje.login.Veri.Kullanici;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final KullaniciRepo kullaniciRepo;

    //Spring Security için bu class zorunlu
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Kullanici kullanici = kullaniciRepo.findByEmail(email);

        if (kullanici == null) {
            throw new UsernameNotFoundException("Kullanıcı bulunamadı: " + email);
        }

        return new CustomUserDetails(kullanici);
    }
}