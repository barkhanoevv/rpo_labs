package com.example.backend.controllers;

import com.example.backend.models.Artist;
import com.example.backend.models.Country;
import com.example.backend.repositories.ArtistRepository;
import com.example.backend.repositories.CountryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api/v1")
public class ArtistController {
    @Autowired
    ArtistRepository artistRepository;

    @GetMapping("/artists")
    public List getAllArtists() {
        return artistRepository.findAll();
    }

    @PostMapping("/artists")
    public ResponseEntity<Object> createArtist(@RequestBody Artist artist) throws ResponseStatusException {
        try {
            Artist nc = artistRepository.save(artist);
            return ResponseEntity.ok(nc);
        } catch (Exception ex) {
            String error;
            if (ex.getMessage().contains("artists.name_UNIQUE"))
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Данный художник уже есть в базе");
            else
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal error");
        }
    }
}
