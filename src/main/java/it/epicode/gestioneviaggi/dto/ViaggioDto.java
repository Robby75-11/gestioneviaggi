package it.epicode.gestioneviaggi.dto;

import it.epicode.gestioneviaggi.enumeration.StatoViaggio;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class ViaggioDto {

    private  Long id;

@NotEmpty(message = "la destinazione non può essere vuota")
    private String destinazione;
@NotNull(message = "la data non può essere nulla")
    private LocalDate data;
@NotNull(message = "lo stato non può essere nullo")
    private StatoViaggio statoViaggio;
}
