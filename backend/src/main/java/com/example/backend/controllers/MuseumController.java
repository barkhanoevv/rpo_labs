package com.example.backend.controllers;

import com.example.backend.models.Museum;
import com.example.backend.repositories.MuseumRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.util.*;

@RestController
@RequestMapping("/api/v1")
public class MuseumController {
    @Autowired
    MuseumRepository museumRepository;

    @GetMapping("/museums")
    public List getAllUMuseums() {
        return museumRepository.findAll();
    }

    @PostMapping("/museums")
    public ResponseEntity<Object> createMuseum(@Valid @RequestBody Museum museum) throws ResponseStatusException {
        try {
            Museum nc = museumRepository.save(museum);
            return ResponseEntity.ok(nc);
        }
        catch (Exception ex)
        {
            String error;
            if (ex.getMessage().contains("museums.name_UNIQUE"))
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Данный музей уже есть в базе");
            else
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Неизвестная ошибка");
        }
    }
}
