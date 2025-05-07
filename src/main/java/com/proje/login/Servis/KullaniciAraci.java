package com.proje.login.Servis;

import com.proje.login.DTO.KullaniciDTO;
import com.proje.login.Veri.Kullanici;

import java.util.List;

public interface KullaniciAraci {
    KullaniciDTO yeniKullanici(KullaniciDTO kullanici);
    Kullanici getirKullanici(int id);
    List<Kullanici> tumKullanicilar();
    Kullanici emailGuncelle(Kullanici kullanici);
    void kullaniciSil(int id);
}
