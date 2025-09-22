package com.itau.cat_api.controller;

import com.itau.cat_api.model.entity.Image;
import com.itau.cat_api.service.ImageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/images")
@CrossOrigin(origins = "*")
public class ImageController {

    private static final Logger logger = LoggerFactory.getLogger(ImageController.class);

    @Autowired
    private ImageService imageService;

    /**
     * Busca imagens por categoria
     */
    @GetMapping("/category/{category}")
    public ResponseEntity<List<Image>> getImagesByCategory(@PathVariable String category) {
        logger.info("GET /api/images/category/{} - Getting images by category", category);

        try {
            Image.ImageCategory imageCategory = Image.ImageCategory.valueOf(category.toUpperCase());
            List<Image> images = imageService.getImagesByCategory(imageCategory);
            logger.info("Found {} images for category: {}", images.size(), category);
            return ResponseEntity.ok(images);
        } catch (IllegalArgumentException e) {
            logger.warn("Invalid category: {}", category);
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Busca imagens por raça
     */
    @GetMapping("/breed/{breedId}")
    public ResponseEntity<List<Image>> getImagesByBreed(@PathVariable String breedId) {
        logger.info("GET /api/images/breed/{} - Getting images by breed", breedId);

        List<Image> images = imageService.getImagesByBreedId(breedId);
        logger.info("Found {} images for breed: {}", images.size(), breedId);
        return ResponseEntity.ok(images);
    }

    /**
     * Endpoint administrativo para coletar imagens de raças
     */
    @PostMapping("/collect/breeds")
    public CompletableFuture<ResponseEntity<String>> collectBreedImages() {
        logger.info("POST /api/images/collect/breeds - Starting breed images collection");

        return imageService.collectAndSaveBreedImages()
                .thenApply(v -> {
                    logger.info("Breed images collection completed successfully");
                    return ResponseEntity.ok("Breed images collected successfully");
                })
                .exceptionally(throwable -> {
                    logger.error("Error collecting breed images: {}", throwable.getMessage());
                    return ResponseEntity.internalServerError().body("Error collecting breed images: " + throwable.getMessage());
                });
    }

    /**
     * Endpoint administrativo para coletar imagens de categorias
     */
    @PostMapping("/collect/categories")
    public CompletableFuture<ResponseEntity<String>> collectCategoryImages() {
        logger.info("POST /api/images/collect/categories - Starting category images collection");

        return imageService.collectAndSaveCategoryImages()
                .thenApply(v -> {
                    logger.info("Category images collection completed successfully");
                    return ResponseEntity.ok("Category images collected successfully");
                })
                .exceptionally(throwable -> {
                    logger.error("Error collecting category images: {}", throwable.getMessage());
                    return ResponseEntity.internalServerError().body("Error collecting category images: " + throwable.getMessage());
                });
    }

}
