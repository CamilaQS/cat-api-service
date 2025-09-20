package com.itau.cat_api.controller;

import com.itau.cat_api.model.entity.Breed;
import com.itau.cat_api.service.BreedService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/breeds")
@CrossOrigin(origins = "*")

public class BreedController {

    private static final Logger logger = LoggerFactory.getLogger(BreedController.class);

    @Autowired
    private BreedService breedService;

    @GetMapping
    public ResponseEntity<List<Breed>> getAllBreeds() {
        logger.info("GET /api/breeds - Listing all breeds");

        List<Breed> breeds = breedService.getAllBreeds();
        logger.info("Returning {} breeds", breeds.size());

        return ResponseEntity.ok(breeds);
    }

    @PostMapping("/collect")
    public CompletableFuture<ResponseEntity<String>> collectBreeds() {
        logger.info("POST /api/breeds/collect - Starting breed collection");

        return breedService.collectAndSaveAllBreeds()
                .thenApply(v -> {
                    logger.info("Breed collection completed successfully");
                    return ResponseEntity.ok("Breeds collected successfully");
                })
                .exceptionally(throwable -> {
                    logger.error("Error collecting breeds: {}", throwable.getMessage());
                    return ResponseEntity.internalServerError().body("Error collecting breeds: " + throwable.getMessage());
                });
    }

 /*   @GetMapping("/by-temperament/{temperament}")
    public CompletableFuture<ResponseEntity<List<Breed>>> getBreedsByTemperament(@PathVariable String temperament){

        return CompletableFuture.supplyAsync(() -> {
            List<Breed> breeds = breedService.getBreedsByTemperament(temperament);
            logger.info("Found {} breeds with temperament: {}", breeds.size(), temperament);
            return ResponseEntity.ok(breeds);
        });

    }
    */

    @GetMapping("/{id}")
    public ResponseEntity<Breed> getBreedById(@PathVariable String id) {
        logger.info("GET /api/breeds/{} - Getting breed by ID", id);

        Optional<Breed> breed = breedService.getBreedsById(id);

        if (breed.isPresent()) {
            logger.info("Breed found: {}", breed.get().getName());
            return ResponseEntity.ok(breed.get());
        } else {
            logger.warn("Breed not found with ID: {}", id);
            return ResponseEntity.notFound().build();
        }
    }


    @GetMapping("/by-temperament/{temperament}")
    public Mono<ResponseEntity<List<Breed>>> getBreedsByTemperament(@PathVariable String temperament) {
        logger.info("Found {} breeds with temperament: {}", temperament);
        return Mono.fromSupplier(() -> breedService.getBreedsByTemperament(temperament))
                .map(ResponseEntity::ok);
    }


    @GetMapping("/by-origin/{origin}")
    public Mono<ResponseEntity<List<Breed>>> getBreedsByOrigin(@PathVariable String origin) {
        logger.info("GET /api/breeds/by-origin/{} - Getting breeds by origin", origin);

        return Mono.fromSupplier(() -> breedService.getBreedsByOrigin(origin)).map(ResponseEntity::ok);

    }



}
