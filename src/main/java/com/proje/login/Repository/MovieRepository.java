package com.proje.login.Repository;

import com.proje.login.Veri.Movie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
 
@Repository
public interface MovieRepository extends JpaRepository<Movie, Long> {
    // You can add custom query methods here if needed
} 