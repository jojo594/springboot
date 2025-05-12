package com.proje.login.Servis;

import com.proje.login.DTO.KullaniciDTO;
import com.proje.login.Repository.KullaniciRepo;
import com.proje.login.Veri.Kullanici;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service @AllArgsConstructor
public class KullaniciAraciImpl implements KullaniciAraci {
    private KullaniciRepo kullaniciRepo;

    @Override
    public KullaniciDTO yeniKullanici(KullaniciDTO kullanici) {
        Kullanici yeniKullanici = new Kullanici(kullanici.getEmail(), kullanici.getPassword(), kullanici.getUsername(), kullanici.getYas());
        Kullanici yeniKayit = kullaniciRepo.save(yeniKullanici);
        return new KullaniciDTO(yeniKayit.getEmail(), yeniKayit.getPassword(), yeniKayit.getUsername(), yeniKayit.getYas());
    }
    @Override
    public Kullanici getirKullanici(int id) {
        Optional<Kullanici> getir = kullaniciRepo.findById(id);
        if (getir.isPresent()) {
            return getir.get();
        }
        return null;
    }

    @Override
    public List<Kullanici> tumKullanicilar() {
        return kullaniciRepo.findAll();
    }
    @Override
    public Kullanici emailGuncelle(Kullanici kullanici) { // Kullanıcı nesnesinden düzenle
        Kullanici edit = kullaniciRepo.findById(kullanici.getId()).get();
        edit.setEmail(kullanici.getEmail());
        Kullanici guncel = kullaniciRepo.save(edit);
        return guncel;
    }
    @Override
    public void kullaniciSil(int id) {
        kullaniciRepo.deleteById(id);
    }
    
    @Override
    public Kullanici kullaniciGuncelle(Kullanici kullanici) {
        // varolan kullanıcıyı bul (eski)
        Optional<Kullanici> existingOptional = kullaniciRepo.findById(kullanici.getId());
        if (!existingOptional.isPresent()) {
            return null;
        }
        
        // yeni kullanıcıyı kaydet
        return kullaniciRepo.save(kullanici);
    }
}
