package com.itau.cat_api.service;

import com.itau.cat_api.model.dto.BreedDTO;
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




}
