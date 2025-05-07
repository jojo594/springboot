package com.proje.login.Veri;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity @NoArgsConstructor
@AllArgsConstructor
@Getter @Setter
@Table(name="kullanici")
public class Kullanici {
    @Id @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    private int id;
    private String email;
    private String password;
    private String username;
    private int yas;
    private String role = "USER"; // Default role
    private LocalDateTime lastLoginDate;

    public Kullanici(String email, String password, String username, int yas) {
        this.email = email;
        this.password = password;
        this.username = username;
        this.yas = yas;
    }
}
