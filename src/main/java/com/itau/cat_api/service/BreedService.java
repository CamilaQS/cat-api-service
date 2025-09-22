package com.itau.cat_api.service;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import com.itau.cat_api.model.dto.BreedDTO;
import com.itau.cat_api.model.entity.Breed;
import com.itau.cat_api.repository.BreedRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
@Transactional
public class BreedService {
    private static final Logger logger = LoggerFactory.getLogger(BreedService.class);

    @Autowired
    private BreedRepository breedRepository;

    @Autowired
    private CatApiClientService catApiClientService;


    public CompletableFuture<Void> collectAndSaveAllBreeds() {
        logger.info("Starting breed collection process");

        return catApiClientService.getAllBreedsAsync()
                .thenApply(this::convertAndSaveBreeds)
                .thenRun(() -> logger.info("Breed collection process completed"));
    }

    private List<Breed> convertAndSaveBreeds(List<BreedDTO> breedDtos) {
        logger.debug("Converting and saving {} breeds", breedDtos.size());

        List<Breed> breeds = breedDtos.stream()
                .map(this::convertToEntity)
                .collect(Collectors.toList());

        return breedRepository.saveAll(breeds);
    }

    private Breed convertToEntity(BreedDTO dto) {
        Breed breed = new Breed();
        breed.setId(dto.getId());
        breed.setName(dto.getName());
        breed.setTemperament(dto.getTemperament());
        breed.setOrigin(dto.getOrigin());
        breed.setDescription(dto.getDescription());
        breed.setLifeSpan(dto.getLifeSpan());
        breed.setWikipediaUrl(dto.getWikipediaUrl());
        breed.setReferenceImageId(dto.getReferenceImageId());
        return breed;
    }

    public List<Breed> getAllBreeds() {
        logger.debug("Fetching all breeds from database");
        return breedRepository.findAll();
    }


    public Optional<Breed> getBreedsById(String id){
        logger.debug("Fetching breeds by temperament: {}", id);
        return breedRepository.findById(id);
    }


    public List<Breed> getBreedsByTemperament(String temperament) {
        logger.debug("Fetching breeds by temperament: {}", temperament);
        return breedRepository.findByTemperamentContainingIgnoreCase(temperament);
    }


    public List<Breed> getBreedsByOrigin(String origin) {
        logger.debug("Fetching breeds by origin: {}", origin);
        return breedRepository.findByOriginIgnoreCase(origin);
    }

}
