package com.proje.login.DTO;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@lombok.Getter @lombok.Setter
@NoArgsConstructor @AllArgsConstructor
public class KullaniciDTO {
    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    private int id;
    private String email;
    private String password;
    private String username;
    private int yas;
    public KullaniciDTO(String email, String password, String username, int yas) {
        this.email = email;
        this.password = password;
        this.username = username;
        this.yas = yas;
    }
}
