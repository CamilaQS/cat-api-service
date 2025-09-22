package com.itau.cat_api.service;

import com.itau.cat_api.model.dto.ImageDTO;
import com.itau.cat_api.model.entity.Breed;
import com.itau.cat_api.model.entity.Image;
import com.itau.cat_api.repository.BreedRepository;
import com.itau.cat_api.repository.ImageRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
@Transactional
public class ImageService {
    private static final Logger logger = LoggerFactory.getLogger(ImageService.class);

    @Autowired
    private ImageRepository imageRepository;

    @Autowired
    private BreedRepository breedRepository;

    @Autowired
    private CatApiClientService catApiClientService;

    /**
     * Coleta e salva imagens para todas as raças
     */
    public CompletableFuture<Void> collectAndSaveBreedImages() {
        logger.info("Starting breed images collection process");

        List<Breed> breeds = breedRepository.findAll();

        List<CompletableFuture<Void>> futures = breeds.stream()
                .map(breed -> catApiClientService.getImagesByBreedAsync(breed.getId(), 3)
                        .thenAccept(imageDtos -> saveBreedImages(breed, imageDtos)))
                .collect(Collectors.toList());

        return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                .thenRun(() -> logger.info("Breed images collection process completed"));
    }

    /**
     * Coleta e salva imagens de categorias (chapéu e óculos)
     */
    public CompletableFuture<Void> collectAndSaveCategoryImages() {
        logger.info("Starting category images collection process");

        CompletableFuture<Void> hatImages = catApiClientService.getImagesByCategoryAsync(1, 3)
                .thenAccept(imageDtos -> saveCategoryImages(imageDtos, Image.ImageCategory.HATS));

        CompletableFuture<Void> sunglassesImages = catApiClientService.getImagesByCategoryAsync(4, 3)
                .thenAccept(imageDtos -> saveCategoryImages(imageDtos, Image.ImageCategory.SUNGLASSES));

        return CompletableFuture.allOf(hatImages, sunglassesImages)
                .thenRun(() -> logger.info("Category images collection process completed"));
    }

    private void saveBreedImages(Breed breed, List<ImageDTO> imageDtos) {
        logger.debug("Saving {} images for breed: {}", imageDtos.size(), breed.getName());

        List<Image> images = imageDtos.stream()
                .map(dto -> convertToEntity(dto, Image.ImageCategory.BREED, breed))
                .collect(Collectors.toList());

        imageRepository.saveAll(images);
    }

    private void saveCategoryImages(List<ImageDTO> imageDtos, Image.ImageCategory category) {
        logger.debug("Saving {} images for category: {}", imageDtos.size(), category);

        List<Image> images = imageDtos.stream()
                .map(dto -> convertToEntity(dto, category, null))
                .collect(Collectors.toList());

        imageRepository.saveAll(images);
    }

    private Image convertToEntity(ImageDTO dto, Image.ImageCategory category, Breed breed) {
        Image image = new Image();
        image.setId(dto.getId());
        image.setUrl(dto.getUrl());
        image.setWidth(dto.getWidth());
        image.setHeight(dto.getHeight());
        image.setCategory(category);
        image.setBreed(breed);
        return image;
    }

    /**
     * Busca imagens por categoria
     */
    public List<Image> getImagesByCategory(Image.ImageCategory category) {
        logger.debug("Fetching images by category: {}", category);
        return imageRepository.findByCategory(category);
    }

    /**
     * Busca imagens por raça
     */
    public List<Image> getImagesByBreedId(String breedId) {
        logger.debug("Fetching images by breed ID: {}", breedId);
        return imageRepository.findByBreedId(breedId);
    }

}
