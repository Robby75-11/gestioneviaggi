package it.epicode.gestioneviaggi.service;

import it.epicode.gestioneviaggi.model.Viaggio;
import it.epicode.gestioneviaggi.dto.ViaggioDto; // Corretto: ViaggioDto
import it.epicode.gestioneviaggi.repository.ViaggioRepository;
import it.epicode.gestioneviaggi.exception.NotFoundException;
import it.epicode.gestioneviaggi.exception.ValidationException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ViaggioService {

    @Autowired
    private ViaggioRepository viaggioRepository;

    // Metodo helper per mappare Entity a DTO
    private ViaggioDto mapToViaggioDto(Viaggio viaggio) {
        ViaggioDto dto = new ViaggioDto();
        dto.setId(viaggio.getId());
        dto.setDestinazione(viaggio.getDestinazione());
        dto.setDataPartenza(viaggio.getDataPartenza());
        dto.setDataRitorno(viaggio.getDataRitorno());
        dto.setCosto(viaggio.getCosto());
        dto.setDescrizione(viaggio.getDescrizione());
        dto.setPostiDisponibili(viaggio.getPostiDisponibili());
        return dto;
    }

    // Metodo helper per mappare DTO a Entity (per la creazione/aggiornamento)
    private Viaggio mapToViaggioEntity(ViaggioDto dto, Viaggio viaggio) {
        viaggio.setDestinazione(dto.getDestinazione());
        viaggio.setDataPartenza(dto.getDataPartenza());
        viaggio.setDataRitorno(dto.getDataRitorno());
        viaggio.setCosto(dto.getCosto());
        viaggio.setDescrizione(dto.getDescrizione());
        viaggio.setPostiDisponibili(dto.getPostiDisponibili());
        return viaggio;
    }

    /**
     * Corrisponde all'operazione SAVE (Crea un nuovo viaggio).
     * @param viaggioDto DTO contenente i dati del viaggio da salvare.
     * @return Il DTO del viaggio appena salvato.
     * @throws ValidationException se le date non sono valide.
     */
    @Transactional
    public ViaggioDto save(ViaggioDto viaggioDto) throws ValidationException {
        if (viaggioDto.getDataPartenza().isAfter(viaggioDto.getDataRitorno())) {
            throw new ValidationException("La data di partenza non può essere successiva alla data di ritorno.");
        }
        if (viaggioDto.getDataPartenza().isBefore(LocalDate.now())) {
            throw new ValidationException("La data di partenza non può essere nel passato.");
        }
        if (viaggioDto.getPostiDisponibili() <= 0) {
            throw new ValidationException("I posti disponibili devono essere maggiori di zero.");
        }

        Viaggio viaggio = new Viaggio();
        viaggio = mapToViaggioEntity(viaggioDto, viaggio);

        Viaggio savedViaggio = viaggioRepository.save(viaggio);
        return mapToViaggioDto(savedViaggio);
    }

    /**
     * Corrisponde all'operazione GET (Recupera tutti i viaggi).
     * @return Una lista di DTO di tutti i viaggi.
     */
    @Transactional(readOnly = true)
    public List<ViaggioDto> get() {
        return viaggioRepository.findAll().stream()
                .map(this::mapToViaggioDto)
                .collect(Collectors.toList());
    }

    /**
     * Corrisponde all'operazione GET (Recupera un viaggio tramite ID).
     * @param id ID del viaggio da recuperare.
     * @return Il DTO del viaggio trovato.
     * @throws NotFoundException se il viaggio non esiste.
     */
    @Transactional(readOnly = true)
    public ViaggioDto get(Long id) throws NotFoundException {
        Viaggio viaggio = viaggioRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Viaggio con ID " + id + " non trovato"));
        return mapToViaggioDto(viaggio);
    }

    /**
     * Corrisponde all'operazione GET (Recupera tutti i viaggi con paginazione).
     * @param pageable Oggetto Pageable per la paginazione.
     * @return Una pagina di DTO di viaggi.
     */
    @Transactional(readOnly = true)
    public Page<ViaggioDto> get(Pageable pageable) {
        return viaggioRepository.findAll(pageable)
                .map(this::mapToViaggioDto);
    }

    /**
     * Corrisponde all'operazione UPDATE (Aggiorna un viaggio esistente).
     * @param id ID del viaggio da aggiornare.
     * @param viaggioDto DTO contenente i nuovi dati del viaggio.
     * @return Il DTO del viaggio aggiornato.
     * @throws NotFoundException se il viaggio non esiste.
     * @throws ValidationException se le date o i posti non sono validi.
     */
    @Transactional
    public ViaggioDto update(Long id, ViaggioDto viaggioDto) throws NotFoundException, ValidationException {
        Viaggio existingViaggio = viaggioRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Viaggio con ID " + id + " non trovato"));

        if (viaggioDto.getDataPartenza().isAfter(viaggioDto.getDataRitorno())) {
            throw new ValidationException("La data di partenza non può essere successiva alla data di ritorno.");
        }
        if (viaggioDto.getDataPartenza().isBefore(LocalDate.now()) && existingViaggio.getDataPartenza().isAfter(LocalDate.now())) {
            throw new ValidationException("Non è possibile spostare una data di partenza nel passato.");
        }
        if (viaggioDto.getPostiDisponibili() <= 0) {
            throw new ValidationException("I posti disponibili devono essere maggiori di zero.");
        }

        existingViaggio = mapToViaggioEntity(viaggioDto, existingViaggio);

        Viaggio updatedViaggio = viaggioRepository.save(existingViaggio);
        return mapToViaggioDto(updatedViaggio);
    }

    /**
     * Corrisponde all'operazione DELETE (Elimina un viaggio).
     * @param id ID del viaggio da eliminare.
     * @throws NotFoundException se il viaggio non esiste.
     */
    @Transactional
    public void delete(Long id) throws NotFoundException {
        if (!viaggioRepository.existsById(id)) {
            throw new NotFoundException("Viaggio con ID " + id + " non trovato");
        }
        viaggioRepository.deleteById(id);
    }
}