package it.epicode.gestioneviaggi.service;

import it.epicode.gestioneviaggi.model.Prenotazione;
import it.epicode.gestioneviaggi.model.Dipendente;
import it.epicode.gestioneviaggi.model.Viaggio;
import it.epicode.gestioneviaggi.dto.PrenotazioneDto; // Corretto: PrenotazioneDto
import it.epicode.gestioneviaggi.repository.PrenotazioneRepository;
import it.epicode.gestioneviaggi.repository.DipendenteRepository; // Necessario per associare il dipendente
import it.epicode.gestioneviaggi.repository.ViaggioRepository;     // Necessario per associare il viaggio
import it.epicode.gestioneviaggi.exception.NotFoundException;
import it.epicode.gestioneviaggi.exception.ValidationException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PrenotazioneService {

    @Autowired
    private PrenotazioneRepository prenotazioneRepository;

    @Autowired
    private DipendenteRepository dipendenteRepository; // Per trovare il dipendente
    @Autowired
    private ViaggioRepository viaggioRepository;       // Per trovare il viaggio

    // Metodo helper per mappare Entity a DTO
    private PrenotazioneDto mapToPrenotazioneDto(Prenotazione prenotazione) {
        PrenotazioneDto dto = new PrenotazioneDto();
        dto.setId(prenotazione.getId());
        dto.setdataPrenotazione(prenotazione.getDataPrenotazione());
        dto.setnumeroPosti(prenotazione.getNumeroPosti());
        if (prenotazione.getDipendente() != null) {
            dto.setDipendenteId(prenotazione.getDipendente().getId());
        }
        if (prenotazione.getViaggio() != null) {
            dto.setViaggioId(prenotazione.getViaggio().getId());
        }
        return dto;
    }

    // Metodo helper per mappare DTO a Entity (per la creazione/aggiornamento)
    private Prenotazione mapToPrenotazioneEntity(PrenotazioneDto dto, Prenotazione prenotazione) {
        prenotazione.setDataPrenotazione(dto.getDataPrenotazione());
        prenotazione.setNumeroPosti(dto.getNumeroPosti());
        return prenotazione;
    }

    /**
     * Corrisponde all'operazione SAVE (Crea una nuova prenotazione).
     * @param prenotazioneDto DTO contenente i dati della prenotazione da salvare.
     * @return Il DTO della prenotazione appena salvata.
     * @throws ValidationException se i dati della prenotazione non sono validi o risorse non disponibili.
     * @throws NotFoundException se il dipendente o il viaggio specificati non esistono.
     */
    @Transactional
    public PrenotazioneDto save(PrenotazioneDto prenotazioneDto) throws ValidationException, NotFoundException {
        if (prenotazioneDto.getDataPrenotazione().isAfter(LocalDate.now())) {
            throw new ValidationException("La data di prenotazione non può essere nel futuro.");
        }
        if (prenotazioneDto.getNumeroPosti() <= 0) {
            throw new ValidationException("Il numero di posti deve essere maggiore di zero.");
        }

        // Trova il dipendente
        Dipendente dipendente = dipendenteRepository.findById(prenotazioneDto.getDipendenteId())
                .orElseThrow(() -> new NotFoundException("Dipendente con ID " + prenotazioneDto.getDipendenteId() + " non trovato."));

        // Trova il viaggio
        Viaggio viaggio = viaggioRepository.findById(prenotazioneDto.getViaggioId())
                .orElseThrow(() -> new NotFoundException("Viaggio con ID " + prenotazioneDto.getViaggioId() + " non trovato.")).getViaggio();

        // Controlla la disponibilità dei posti nel viaggio
        if (viaggio.getNumeroposti() < prenotazioneDto.getNumeroPosti()) {
            throw new ValidationException("Non ci sono abbastanza posti disponibili per il viaggio selezionato.");
        }

        // Crea la prenotazione
        Prenotazione prenotazione = new Prenotazione();
        prenotazione = mapToPrenotazioneEntity(prenotazioneDto, prenotazione);
        prenotazione.setDipendente(dipendente);
        prenotazione.setViaggio(viaggio);

        // Aggiorna i posti disponibili nel viaggio
        viaggio.setPostiDisponibili(viaggio.getPostiDisponibili() - prenotazioneDto.getNumeroPosti());
        viaggioRepository.save(viaggio); // Salva l'aggiornamento dei posti nel viaggio

        Prenotazione savedPrenotazione = prenotazioneRepository.save(prenotazione);
        return mapToPrenotazioneDto(savedPrenotazione);
    }

    /**
     * Corrisponde all'operazione GET (Recupera tutte le prenotazioni).
     * @return Una lista di DTO di tutte le prenotazioni.
     */
    @Transactional(readOnly = true)
    public List<PrenotazioneDto> get() {
        return prenotazioneRepository.findAll().stream()
                .map(this::mapToPrenotazioneDto)
                .collect(Collectors.toList());
    }

    /**
     * Corrisponde all'operazione GET (Recupera una prenotazione tramite ID).
     * @param id ID della prenotazione da recuperare.
     * @return Il DTO della prenotazione trovata.
     * @throws NotFoundException se la prenotazione non esiste.
     */
    @Transactional(readOnly = true)
    public PrenotazioneDto get(Long id) throws NotFoundException {
        Prenotazione prenotazione = prenotazioneRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Prenotazione con ID " + id + " non trovata"));
        return mapToPrenotazioneDto(prenotazione);
    }

    /**
     * Corrisponde all'operazione GET (Recupera tutte le prenotazioni con paginazione).
     * @param pageable Oggetto Pageable per la paginazione.
     * @return Una pagina di DTO di prenotazioni.
     */
    @Transactional(readOnly = true)
    public Page<PrenotazioneDto> get(Pageable pageable) {
        return prenotazioneRepository.findAll(pageable)
                .map(this::mapToPrenotazioneDto);
    }

    /**
     * Corrisponde all'operazione UPDATE (Aggiorna una prenotazione esistente).
     * @param id ID della prenotazione da aggiornare.
     * @param prenotazioneDto DTO contenente i nuovi dati della prenotazione.
     * @return Il DTO della prenotazione aggiornata.
     * @throws NotFoundException se la prenotazione, il dipendente o il viaggio non esistono.
     * @throws ValidationException se i dati della prenotazione non sono validi o risorse non disponibili.
     */
    @Transactional
    public PrenotazioneDto update(Long id, PrenotazioneDto prenotazioneDto) throws NotFoundException, ValidationException {
        Prenotazione existingPrenotazione = prenotazioneRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Prenotazione con ID " + id + " non trovata"));

        // Se cambiano dipendenteId o viaggioId, ritroviamo le entità
        Dipendente newDipendente = existingPrenotazione.getDipendente();
        if (!newDipendente.getId().equals(prenotazioneDto.getDipendenteId())) {
            newDipendente = dipendenteRepository.findById(prenotazioneDto.getDipendenteId())
                    .orElseThrow(() -> new NotFoundException("Nuovo Dipendente con ID " + prenotazioneDto.getDipendenteId() + " non trovato."));
        }

        Viaggio oldViaggio = existingPrenotazione.getViaggio();
        Viaggio newViaggio = existingPrenotazione.getViaggio();
        int oldPosti = existingPrenotazione.getNumeroPosti();

        if (!newViaggio.getId().equals(prenotazioneDto.getViaggioId())) {
            newViaggio = viaggioRepository.findById(prenotazioneDto.getViaggioId())
                    .orElseThrow(() -> new NotFoundException("Nuovo Viaggio con ID " + prenotazioneDto.getViaggioId() + " non trovato."));
            // Ripristina posti del vecchio viaggio se il viaggio è cambiato
            oldViaggio.setPostiDisponibili(oldViaggio.getPostiDisponibili() + oldPosti);
            viaggioRepository.save(oldViaggio);
        }

        // Validazioni
        if (prenotazioneDto.getDataPrenotazione().isAfter(LocalDate.now())) {
            throw new ValidationException("La data di prenotazione non può essere nel futuro.");
        }
        if (prenotazioneDto.getNumeroPosti() <= 0) {
            throw new ValidationException("Il numero di posti deve essere maggiore di zero.");
        }

        // Gestione posti disponibili per il nuovo/stesso viaggio
        int deltaPosti = prenotazioneDto.getNumeroPosti() - oldPosti;

        if (newViaggio.getPostiDisponibili() < deltaPosti) { // Se ho bisogno di più posti di quanti ce ne siano
            throw new ValidationException("Non ci sono abbastanza posti disponibili per il numero richiesto nel viaggio selezionato.");
        }

        newViaggio.setPostiDisponibili(newViaggio.getPostiDisponibili() - deltaPosti);
        viaggioRepository.save(newViaggio); // Salva l'aggiornamento dei posti

        // Aggiorna la prenotazione
        existingPrenotazione = mapToPrenotazioneEntity(prenotazioneDto, existingPrenotazione);
        existingPrenotazione.setDipendente(newDipendente);
        existingPrenotazione.setViaggio(newViaggio);

        Prenotazione updatedPrenotazione = prenotazioneRepository.save(existingPrenotazione);
        return mapToPrenotazioneDto(updatedPrenotazione);
    }


    /**
     * Corrisponde all'operazione DELETE (Elimina una prenotazione).
     * @param id ID della prenotazione da eliminare.
     * @throws NotFoundException se la prenotazione non esiste.
     */
    @Transactional
    public void delete(Long id) throws NotFoundException {
        Prenotazione prenotazione = prenotazioneRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Prenotazione con ID " + id + " non trovata"));

        // Ripristina i posti disponibili del viaggio
        Viaggio viaggio = prenotazione.getViaggio();
        if (viaggio != null) {
            viaggio.setPostiDisponibili(viaggio.getPostiDisponibili() + prenotazione.getNumeroPosti());
            viaggioRepository.save(viaggio);
        }

        prenotazioneRepository.deleteById(id);
    }
}