

package it.epicode.gestioneviaggi.controller;

import it.epicode.gestioneviaggi.dto.DipendenteDto;
import it.epicode.gestioneviaggi.service.DipendenteService;
import it.epicode.gestioneviaggi.exception.NotFoundException;
import it.epicode.gestioneviaggi.exception.ValidationException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/dipendenti") // Maps all requests starting with /api/dipendenti
public class DipendenteController {

    @Autowired
    private DipendenteService dipendenteService;


    @PostMapping
    public ResponseEntity<DipendenteDto> registerDipendente(@RequestBody DipendenteDto dipendenteDto) {
        try {

            DipendenteDto createdDipendente = dipendenteService.save(dipendenteDto);
            return new ResponseEntity<>(createdDipendente, HttpStatus.CREATED);
        } catch (ValidationException e) {

            return new ResponseEntity(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }


    @GetMapping

    public ResponseEntity<List<DipendenteDto>> getAllDipendenti() {
        List<DipendenteDto> dipendenti = dipendenteService.get();
        return new ResponseEntity<>(dipendenti, HttpStatus.OK);
    }


    @GetMapping("/{id}")

    public ResponseEntity<DipendenteDto> getDipendenteById(@PathVariable Long id) {
        try {
            // In a real application, there would be logic here to verify
            // that the authenticated user has permission to view this ID.
            // E.g.: If not ADMIN, the requested ID must be equal to the authenticated user's ID.
            DipendenteDto dipendente = dipendenteService.get(id);
            return new ResponseEntity<>(dipendente, HttpStatus.OK);
        } catch (NotFoundException e) {
            return new ResponseEntity(e.getMessage(), HttpStatus.NOT_FOUND); // 404 Not Found
        }
    }


    @GetMapping("/page")

    public ResponseEntity<Page<DipendenteDto>> getAllDipendentiPaged(Pageable pageable) {
        Page<DipendenteDto> dipendentiPage = dipendenteService.get(pageable);
        return new ResponseEntity<>(dipendentiPage, HttpStatus.OK);
    }


    @PutMapping("/{id}")

    public ResponseEntity<DipendenteDto> updateDipendente(@PathVariable Long id, @RequestBody DipendenteDto dipendenteDto) {
        try {
            // As with GET, authorization logic should verify permissions here.
            DipendenteDto updatedDipendente = dipendenteService.update(id, dipendenteDto);
            return new ResponseEntity<>(updatedDipendente, HttpStatus.OK);
        } catch (NotFoundException e) {
            return new ResponseEntity(e.getMessage(), HttpStatus.NOT_FOUND); // 404 Not Found
        } catch (ValidationException e) {
            return new ResponseEntity(e.getMessage(), HttpStatus.BAD_REQUEST); // 400 Bad Request
        }
    }


    @PatchMapping("/{id}/immagine")

    public ResponseEntity<DipendenteDto> uploadImmagineProfilo(
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file) { // Accepts the image file
        try {
            // Again, authorization logic is crucial here.
            DipendenteDto updatedDipendente = dipendenteService.updateImmagineProfilo(id, file);
            return new ResponseEntity<>(updatedDipendente, HttpStatus.OK);
        } catch (NotFoundException e) {
            return new ResponseEntity(e.getMessage(), HttpStatus.NOT_FOUND); // 404 Not Found
        } catch (Exception e) { // Catches IOException or other upload exceptions
            return new ResponseEntity("Error uploading image: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR); // 500 Internal Server Error
        }
    }



    @DeleteMapping("/{id}")

    public ResponseEntity<Void> deleteDipendente(@PathVariable Long id) {
        try {
            dipendenteService.delete(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT); // 204 No Content
        } catch (NotFoundException e) {
            return new ResponseEntity(e.getMessage(), HttpStatus.NOT_FOUND); // 404 Not Found
        }
    }
}