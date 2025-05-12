package com.proje.login.Repository;

import com.proje.login.Veri.Kullanici;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface KullaniciRepo extends JpaRepository<Kullanici,Integer> {
    Kullanici findByEmail(String email);
    List<Kullanici> findByRole(String role);
}
