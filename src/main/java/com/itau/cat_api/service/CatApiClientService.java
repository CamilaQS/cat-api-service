package com.itau.cat_api.service;

import com.itau.cat_api.model.dto.BreedDTO;
import com.itau.cat_api.model.dto.ImageDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
public class CatApiClientService {

    private static final Logger logger = LoggerFactory.getLogger(CatApiClientService.class);

    private final WebClient webClient;
    private final String apiKey;

    public CatApiClientService(WebClient.Builder webClientBuilder,
                               @Value("${catapi.base-url}") String baseUrl,
                               @Value("${catapi.api-key}") String apiKey) {
        this.webClient = webClientBuilder.baseUrl(baseUrl).build();
        this.apiKey = apiKey;
        logger.info("CatApiClientService initialized with base URL: {}", baseUrl);
    }
    public CompletableFuture<List<BreedDTO>> getAllBreedsAsync() {
        logger.debug("Fetching all breeds from The Cat API");

        return webClient.get()
                .uri("/breeds")
                .header("x-api-key", apiKey)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<BreedDTO>>() {})
                .doOnSuccess(breeds -> logger.info("Successfully fetched {} breeds", breeds.size()))
                .doOnError(error -> logger.error("Error fetching breeds: {}", error.getMessage()))
                .toFuture();
    }



    public CompletableFuture<List<ImageDTO>> getImagesByBreedAsync(String breedId, int limit) {
        logger.debug("Fetching {} images for breed: {}", limit, breedId);

        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/images/search")
                        .queryParam("breed_ids", breedId)
                        .queryParam("limit", limit)
                        .build())
                .header("x-api-key", apiKey)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<ImageDTO>>() {})
                .doOnSuccess(images -> logger.info("Successfully fetched {} images for breed {}", images.size(), breedId))
                .doOnError(error -> logger.error("Error fetching images for breed {}: {}", breedId, error.getMessage()))
                .toFuture();
    }


    public CompletableFuture<List<ImageDTO>> getImagesByCategoryAsync(int categoryId, int limit) {
        logger.debug("Fetching {} images for category: {}", limit, categoryId);

        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/images/search")
                        .queryParam("category_ids", categoryId)
                        .queryParam("limit", limit)
                        .build())
                .header("x-api-key", apiKey)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<ImageDTO>>() {})
                .doOnSuccess(images -> logger.info("Successfully fetched {} images for category {}", images.size(), categoryId))
                .doOnError(error -> logger.error("Error fetching images for category {}: {}", categoryId, error.getMessage()))
                .toFuture();
    }


    public CompletableFuture<List<ImageDTO>> getAllCategoryImagesAsync() {
        logger.debug("Fetching images for all categories in parallel");

        CompletableFuture<List<ImageDTO>> hatImages = getImagesByCategoryAsync(1, 3); // Hats
        CompletableFuture<List<ImageDTO>> sunglassesImages = getImagesByCategoryAsync(4, 3); // Sunglasses


        return CompletableFuture.allOf(hatImages, sunglassesImages)
                .thenApply(v -> {
                    List<ImageDTO> allImages = hatImages.join();
                    allImages.addAll(sunglassesImages.join());
                    logger.info("Successfully fetched {} total category images", allImages.size());
                    return allImages;
                });
    }


}
