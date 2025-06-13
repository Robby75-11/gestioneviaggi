package it.epicode.gestioneviaggi.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data

public class Dipendente {

    @Id
    @GeneratedValue
    private int Id;
    private String username;
    private String nome;
    private String cognome;
    private String email;

    private String immagineProfiloUrl;


}
