package com.itau.cat_api.controller;

import com.itau.cat_api.model.entity.Breed;
import com.itau.cat_api.service.BreedService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.itau.cat_api.controller.BreedController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Testes unitários para BreedController
 *
 * Esta classe usa @WebMvcTest para testar apenas a camada web (controller),
 * mockando as dependências de serviço. Isso permite testar o comportamento
 * HTTP, serialização JSON e mapeamento de endpoints de forma isolada.
 */
@WebMvcTest(BreedController.class)
@DisplayName("BreedController Tests")
class BreedControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private BreedService breedService;

    @Autowired
    private ObjectMapper objectMapper;

    private Breed sampleBreed;
    private List<Breed> sampleBreeds;

    @BeforeEach
    void setUp() {
        // Arrange: Preparar dados de teste
        sampleBreed = new Breed();
        sampleBreed.setId("abys");
        sampleBreed.setName("Abyssinian");
        sampleBreed.setTemperament("Active, Energetic, Independent");
        sampleBreed.setOrigin("Egypt");
        sampleBreed.setDescription("The Abyssinian is easy to care for...");

        Breed secondBreed = new Breed();
        secondBreed.setId("beng");
        secondBreed.setName("Bengal");
        secondBreed.setTemperament("Alert, Agile, Energetic");
        secondBreed.setOrigin("United States");
        secondBreed.setDescription("Bengals are a lot of fun to live with...");

        sampleBreeds = Arrays.asList(sampleBreed, secondBreed);
    }

    @Test
    @DisplayName("GET /api/breeds should return all breeds")
    void getAllBreeds_shouldReturnAllBreeds() throws Exception {
        // Arrange
        when(breedService.getAllBreeds()).thenReturn(sampleBreeds);

        // Act & Assert
        mockMvc.perform(get("/api/breeds")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is("abys")))
                .andExpect(jsonPath("$[0].name", is("Abyssinian")))
                .andExpect(jsonPath("$[0].origin", is("Egypt")))
                .andExpect(jsonPath("$[1].id", is("beng")))
                .andExpect(jsonPath("$[1].name", is("Bengal")))
                .andExpect(jsonPath("$[1].origin", is("United States")));

        verify(breedService, times(1)).getAllBreeds();
    }

    @Test
    @DisplayName("GET /api/breeds should return empty list when no breeds exist")
    void getAllBreeds_shouldReturnEmptyList_whenNoBreedsExist() throws Exception {
        // Arrange
        when(breedService.getAllBreeds()).thenReturn(Arrays.asList());

        // Act & Assert
        mockMvc.perform(get("/api/breeds")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(0)));

        verify(breedService, times(1)).getAllBreeds();
    }

    @Test
    @DisplayName("GET /api/breeds/{id} should return breed when valid ID is provided")
    void getBreedById_shouldReturnBreed_whenValidIdIsProvided() throws Exception {
        // Arrange
        String breedId = "abys";
        when(breedService.getBreedsById(breedId)).thenReturn(Optional.of(sampleBreed));

        // Act & Assert
        mockMvc.perform(get("/api/breeds/{id}", breedId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is("abys")))
                .andExpect(jsonPath("$.name", is("Abyssinian")))
                .andExpect(jsonPath("$.temperament", is("Active, Energetic, Independent")))
                .andExpect(jsonPath("$.origin", is("Egypt")))
                .andExpect(jsonPath("$.description", containsString("Abyssinian")));

        verify(breedService, times(1)).getBreedsById(breedId);
    }

    @Test
    @DisplayName("GET /api/breeds/{id} should return 404 when breed not found")
    void getBreedById_shouldReturn404_whenBreedNotFound() throws Exception {
        // Arrange
        String invalidId = "invalid";
        when(breedService.getBreedsById(invalidId)).thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(get("/api/breeds/{id}", invalidId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound());

        verify(breedService, times(1)).getBreedsById(invalidId);
    }

    @Test
    @DisplayName("GET /api/breeds/by-temperament/{temperament} should return breeds by temperament")
    void getBreedsByTemperament_shouldReturnBreedsByTemperament() throws Exception {
        // Arrange
        String temperament = "active";
        List<Breed> filteredBreeds = Arrays.asList(sampleBreed);
        when(breedService.getBreedsByTemperament(temperament)).thenReturn(filteredBreeds);

    }


    @Test
    @DisplayName("Should return correct Content-Type headers")
    void shouldReturnCorrectContentTypeHeaders() throws Exception {
        // Arrange
        when(breedService.getAllBreeds()).thenReturn(sampleBreeds);

        // Act & Assert
        mockMvc.perform(get("/api/breeds"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", "application/json"));

        verify(breedService, times(1)).getAllBreeds();
    }

    @Test
    @DisplayName("Should handle CORS preflight requests")
    void shouldHandleCorsPreflightRequests() throws Exception {
        // Act & Assert
        mockMvc.perform(options("/api/breeds")
                        .header("Origin", "http://localhost:3000")
                        .header("Access-Control-Request-Method", "GET"))
                .andDo(print())
                .andExpect(status().isOk());
    }
}

