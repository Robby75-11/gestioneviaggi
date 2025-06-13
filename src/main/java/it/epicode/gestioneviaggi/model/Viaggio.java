package it.epicode.gestioneviaggi.model;

import it.epicode.gestioneviaggi.enumeration.StatoViaggio;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Entity
@Data

public class Viaggio {

    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false)
    private String destinazione;

    @Column(nullable = false)
    private LocalDate data;

    @Enumerated(EnumType.STRING)
    private StatoViaggio statoViaggio;

    @Column(nullable = false) // Potresti volerlo nullable o con un default
    private int postiDisponibili;
}
