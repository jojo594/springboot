package com.proje.login.DTO;

import com.proje.login.Veri.Kullanici;

public class KullaniciEsle {
    public static KullaniciDTO kullaniciDTOEsle(Kullanici kullanici) {
        if (kullanici == null) return null;
        KullaniciDTO dto = new KullaniciDTO();
        dto.setId(kullanici.getId());
        dto.setUsername(kullanici.getUsername());
        dto.setEmail(kullanici.getEmail());
        dto.setPassword(kullanici.getPassword());
        return dto;
    }

    public static Kullanici kullaniciEsle(KullaniciDTO kullaniciDTO) {
        if (kullaniciDTO == null) return null;
        Kullanici kullanici = new Kullanici();
        kullanici.setId(kullaniciDTO.getId());
        kullanici.setUsername(kullaniciDTO.getUsername());
        kullanici.setEmail(kullaniciDTO.getEmail());
        kullanici.setPassword(kullaniciDTO.getPassword());
        return kullanici;
    }
}
