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
    private String destinazione;
    private LocalDate data;

    @Enumerated(EnumType.STRING)
    private StatoViaggio statoviaggio;
}
