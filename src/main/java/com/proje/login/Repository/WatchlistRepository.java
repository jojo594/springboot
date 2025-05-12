package com.proje.login.Repository;

import com.proje.login.Veri.Kullanici;
import com.proje.login.Veri.Watchlist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WatchlistRepository extends JpaRepository<Watchlist, Long> {
    List<Watchlist> findByUser(Kullanici user);
} 