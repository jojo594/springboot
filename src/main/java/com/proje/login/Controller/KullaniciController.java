package com.proje.login.Controller;

import com.proje.login.DTO.KullaniciDTO;
import com.proje.login.Servis.KullaniciAraci;
import com.proje.login.Veri.Kullanici;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController @AllArgsConstructor
public class KullaniciController {
    private KullaniciAraci kullaniciAraci;

    @PostMapping("ilkKayit")
    public ResponseEntity<KullaniciDTO> kullaniciEkle(@RequestBody KullaniciDTO kullanici){
        KullaniciDTO yeni = kullaniciAraci.yeniKullanici(kullanici);
        return new ResponseEntity<>(yeni, HttpStatus.CREATED);
    }
    @GetMapping("/getir/{id}")
    public ResponseEntity<Kullanici> kullaniciGetir(@PathVariable int id){
        Kullanici gelen = kullaniciAraci.getirKullanici(id);
        if (gelen == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND); // id'den bulunamadıysa hata, belki başka şekilde yapılır
        }
        return new ResponseEntity<>(gelen, HttpStatus.OK);
    }
    @GetMapping("/tumKullanicilar")
    public ResponseEntity<List<Kullanici>> tumKullanicilar(){
        return new ResponseEntity<>(kullaniciAraci.tumKullanicilar(), HttpStatus.OK);
    }

    @PostMapping("/kullaniciSil/{id}")
    public ResponseEntity<String> kullaniciSil(@PathVariable("id") int id){
        kullaniciAraci.kullaniciSil(id);
        return new ResponseEntity<>("Silindi", HttpStatus.OK);
    }
}
