package it.epicode.gestioneviaggi.controller;

import it.epicode.gestioneviaggi.dto.PrenotazioneDto;
import it.epicode.gestioneviaggi.service.PrenotazioneService;
import it.epicode.gestioneviaggi.exception.NotFoundException;
import it.epicode.gestioneviaggi.exception.ValidationException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/prenotazioni")
public class PrenotazioneController {

    @Autowired
    private PrenotazioneService prenotazioneService;

    /**
     * Endpoint per la creazione di una nuova prenotazione.
     * Accessibile a tutti gli utenti autenticati.
     * POST /api/prenotazioni
     * @param prenotazioneDto DTO della prenotazione da creare.
     * @return ResponseEntity con il DTO della prenotazione creata e status 201.
     */
    @PostMapping

    public ResponseEntity<PrenotazioneDto> createPrenotazione(@RequestBody PrenotazioneDto prenotazioneDto) {
        try {
            PrenotazioneDto createdPrenotazione = prenotazioneService.save(prenotazioneDto);
            return new ResponseEntity<>(createdPrenotazione, HttpStatus.CREATED);
        } catch (NotFoundException e) {
            return new ResponseEntity(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (ValidationException e) {
            return new ResponseEntity(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Endpoint per il recupero di tutte le prenotazioni.
     * Accessibile agli ADMIN.
     * GET /api/prenotazioni
     * @return ResponseEntity con la lista di DTO delle prenotazioni e status 200.
     */
    @GetMapping

    public ResponseEntity<List<PrenotazioneDto>> getAllPrenotazioni() {
        List<PrenotazioneDto> prenotazioni = prenotazioneService.get();
        return new ResponseEntity<>(prenotazioni, HttpStatus.OK);
    }

    /**
     * Endpoint per il recupero di una prenotazione tramite ID.
     * Accessibile agli ADMIN o all'utente proprietario della prenotazione (logica da implementare nel service).
     * GET /api/prenotazioni/{id}
     * @param id ID della prenotazione.
     * @return ResponseEntity con il DTO della prenotazione e status 200.
     */
    @GetMapping("/{id}")

    public ResponseEntity<PrenotazioneDto> getPrenotazioneById(@PathVariable Long id) {
        try {
            PrenotazioneDto prenotazione = prenotazioneService.get(id);
            // Potresti aggiungere logica qui per verificare se l'utente autenticato è il proprietario della prenotazione
            return new ResponseEntity<>(prenotazione, HttpStatus.OK);
        } catch (NotFoundException e) {
            return new ResponseEntity(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Endpoint per il recupero di tutte le prenotazioni con paginazione.
     * Accessibile agli ADMIN.
     * GET /api/prenotazioni/page?page=0&size=10&sort=dataPrenotazione,desc
     * @param pageable Oggetto Pageable per la paginazione e l'ordinamento.
     * @return ResponseEntity con una pagina di DTO di prenotazioni e status 200.
     */
    @GetMapping("/page")

    public ResponseEntity<Page<PrenotazioneDto>> getAllPrenotazioniPaged(Pageable pageable) {
        Page<PrenotazioneDto> prenotazioniPage = prenotazioneService.get(pageable);
        return new ResponseEntity<>(prenotazioniPage, HttpStatus.OK);
    }

    /**
     * Endpoint per l'aggiornamento di una prenotazione esistente.
     * Accessibile agli ADMIN o all'utente proprietario della prenotazione (logica da implementare nel service).
     * PUT /api/prenotazioni/{id}
     * @param id ID della prenotazione da aggiornare.
     * @param prenotazioneDto DTO con i dati aggiornati della prenotazione.
     * @return ResponseEntity con il DTO della prenotazione aggiornata e status 200.
     */
    @PutMapping("/{id}")

    public ResponseEntity<PrenotazioneDto> updatePrenotazione(@PathVariable Long id, @RequestBody PrenotazioneDto prenotazioneDto) {
        try {
            PrenotazioneDto updatedPrenotazione = prenotazioneService.update(id, prenotazioneDto);
            // Potresti aggiungere logica qui per verificare se l'utente autenticato è il proprietario della prenotazione
            return new ResponseEntity<>(updatedPrenotazione, HttpStatus.OK);
        } catch (NotFoundException e) {
            return new ResponseEntity(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (ValidationException e) {
            return new ResponseEntity(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Endpoint per l'eliminazione di una prenotazione.
     * Accessibile agli ADMIN.
     * DELETE /api/prenotazioni/{id}
     * @param id ID della prenotazione da eliminare.
     * @return ResponseEntity con status 204 (No Content).
     */
    @DeleteMapping("/{id}")

    public ResponseEntity<Void> deletePrenotazione(@PathVariable Long id) {
        try {
            prenotazioneService.delete(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT); // 204 No Content
        } catch (NotFoundException e) {
            return new ResponseEntity(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }
}