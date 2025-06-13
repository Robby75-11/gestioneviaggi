package it.epicode.gestioneviaggi.repository;

import it.epicode.gestioneviaggi.model.Dipendente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.Optional;

public interface DipendenteRepository  extends JpaRepository<Dipendente, Long>, PagingAndSortingRepository<Dipendente, Long> {
    Optional<Dipendente> findByUsername(String username); // <--- DEVE ESSERE PRESENTE
    Optional<Dipendente> findByEmail(String email);       // <--- DEVE ESSERE PRESENTE
}

