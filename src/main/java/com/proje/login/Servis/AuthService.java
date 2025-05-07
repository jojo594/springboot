package com.proje.login.Servis;

import com.proje.login.DTO.AuthDTO;
import com.proje.login.Veri.Kullanici;

public interface AuthService {
    Kullanici login(AuthDTO authDTO);
    void logout();
    Kullanici getCurrentUser();
    Kullanici register(Kullanici kullanici);
} 