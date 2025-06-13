package it.epicode.gestioneviaggi.repository;

import it.epicode.gestioneviaggi.model.Prenotazione;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface ViaggioRepository extends JpaRepository<Prenotazione, Long>{
}
