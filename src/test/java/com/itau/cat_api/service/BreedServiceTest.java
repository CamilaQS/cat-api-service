package com.itau.cat_api.service;

import com.itau.cat_api.model.dto.BreedDTO;
import com.itau.cat_api.model.entity.Breed;
import com.itau.cat_api.repository.BreedRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("BreedService Tests")
public class BreedServiceTest {

    @Mock
    private BreedRepository breedRepository;

    @Mock
    private CatApiClientService catApiClientService;

    @InjectMocks
    private BreedService breedService;

    @Captor
    private ArgumentCaptor<List<Breed>> breedListCaptor;

    private Breed sampleBreed;
    private BreedDTO sampleBreedDto;
    private List<Breed> sampleBreeds;

    @BeforeEach
    void setUp() {
        // Arrange: Preparar dados de teste que serão reutilizados
        sampleBreed = new Breed();
        sampleBreed.setId("abys");
        sampleBreed.setName("Abyssinian");
        sampleBreed.setTemperament("Active, Energetic, Independent");
        sampleBreed.setOrigin("Egypt");
        sampleBreed.setDescription("The Abyssinian is easy to care for...");

        sampleBreedDto = new BreedDTO();
        sampleBreedDto.setId("abys");
        sampleBreedDto.setName("Abyssinian");
        sampleBreedDto.setTemperament("Active, Energetic, Independent");
        sampleBreedDto.setOrigin("Egypt");
        sampleBreedDto.setDescription("The Abyssinian is easy to care for...");

        Breed secondBreed = new Breed();
        secondBreed.setId("beng");
        secondBreed.setName("Bengal");
        secondBreed.setTemperament("Alert, Agile, Energetic");
        secondBreed.setOrigin("United States");
        secondBreed.setDescription("Bengals are a lot of fun to live with...");

        sampleBreeds = Arrays.asList(sampleBreed, secondBreed);
    }

    @Test
    @DisplayName("Should return all breeds when getAllBreeds is called")
    void shouldReturnAllBreeds_whenGetAllBreedsIsCalled() {
        // Arrange
        when(breedRepository.findAll()).thenReturn(sampleBreeds);

        // Act
        List<Breed> result = breedService.getAllBreeds();

        // Assert
        assertThat(result).isNotNull();
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getName()).isEqualTo("Abyssinian");
        assertThat(result.get(1).getName()).isEqualTo("Bengal");

        // Verify that the repository method was called exactly once
        verify(breedRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Should return breed when valid ID is provided")
    void shouldReturnBreed_whenValidIdIsProvided() {
        // Arrange
        String breedId = "abys";
        when(breedRepository.findById(breedId)).thenReturn(Optional.of(sampleBreed));

        // Act
        Optional<Breed> result = breedService.getBreedsById(breedId);

        // Assert
        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(breedId);
        assertThat(result.get().getName()).isEqualTo("Abyssinian");
        assertThat(result.get().getOrigin()).isEqualTo("Egypt");

        verify(breedRepository, times(1)).findById(breedId);
    }

    @Test
    @DisplayName("Should return empty when invalid ID is provided")
    void shouldReturnEmpty_whenInvalidIdIsProvided() {
        // Arrange
        String invalidId = "invalid";
        when(breedRepository.findById(invalidId)).thenReturn(Optional.empty());

        // Act
        Optional<Breed> result = breedService.getBreedsById(invalidId);

        // Assert
        assertThat(result).isEmpty();

        verify(breedRepository, times(1)).findById(invalidId);
    }

    @Test
    @DisplayName("Should return breeds by temperament when valid temperament is provided")
    void shouldReturnBreedsByTemperament_whenValidTemperamentIsProvided() {
        // Arrange
        String temperament = "active";
        List<Breed> expectedBreeds = Arrays.asList(sampleBreed);
        when(breedRepository.findByTemperamentContainingIgnoreCase(temperament))
                .thenReturn(expectedBreeds);

        // Act
        List<Breed> result = breedService.getBreedsByTemperament(temperament);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTemperament()).containsIgnoringCase("active");

        verify(breedRepository, times(1)).findByTemperamentContainingIgnoreCase(temperament);
    }

    @Test
    @DisplayName("Should return empty list when no breeds match temperament")
    void shouldReturnEmptyList_whenNoBreedsMatchTemperament() {
        // Arrange
        String temperament = "nonexistent";
        when(breedRepository.findByTemperamentContainingIgnoreCase(temperament))
                .thenReturn(Arrays.asList());

        // Act
        List<Breed> result = breedService.getBreedsByTemperament(temperament);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result).isEmpty();

        verify(breedRepository, times(1)).findByTemperamentContainingIgnoreCase(temperament);
    }

    @Test
    @DisplayName("Should return breeds by origin when valid origin is provided")
    void shouldReturnBreedsByOrigin_whenValidOriginIsProvided() {
        // Arrange
        String origin = "Egypt";
        List<Breed> expectedBreeds = Arrays.asList(sampleBreed);
        when(breedRepository.findByOriginIgnoreCase(origin)).thenReturn(expectedBreeds);

        // Act
        List<Breed> result = breedService.getBreedsByOrigin(origin);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getOrigin()).isEqualToIgnoringCase(origin);

        verify(breedRepository, times(1)).findByOriginIgnoreCase(origin);
    }

    @Test
    @DisplayName("Should collect and save breeds successfully")
    void shouldCollectAndSaveBreedsSuccessfully() throws Exception {
        // Arrange
        List<BreedDTO> breedDtos = Arrays.asList(sampleBreedDto);
        CompletableFuture<List<BreedDTO>> futureBreeds = CompletableFuture.completedFuture(breedDtos);

        when(catApiClientService.getAllBreedsAsync()).thenReturn(futureBreeds);
        when(breedRepository.saveAll(any(List.class))).thenReturn(sampleBreeds);

        // Act
        CompletableFuture<Void> result = breedService.collectAndSaveAllBreeds();
        result.get(); // Wait for completion

        // Assert
        verify(catApiClientService, times(1)).getAllBreedsAsync();
        verify(breedRepository, times(1)).saveAll(breedListCaptor.capture());

        List<Breed> savedBreeds = breedListCaptor.getValue();
        assertThat(savedBreeds).hasSize(1);
        assertThat(savedBreeds.get(0).getId()).isEqualTo("abys");
        assertThat(savedBreeds.get(0).getName()).isEqualTo("Abyssinian");
    }



    @Test
    @DisplayName("Should convert BreedDto to Breed entity correctly")
    void shouldConvertBreedDtoToBreedEntityCorrectly() throws Exception {
        // Arrange
        List<BreedDTO> breedDtos = Arrays.asList(sampleBreedDto);
        CompletableFuture<List<BreedDTO>> futureBreeds = CompletableFuture.completedFuture(breedDtos);

        when(catApiClientService.getAllBreedsAsync()).thenReturn(futureBreeds);
        when(breedRepository.saveAll(any(List.class))).thenReturn(sampleBreeds);

        // Act
        CompletableFuture<Void> result = breedService.collectAndSaveAllBreeds();
        result.get(); // Wait for completion

        // Assert
        verify(breedRepository, times(1)).saveAll(breedListCaptor.capture());

        List<Breed> convertedBreeds = breedListCaptor.getValue();
        Breed convertedBreed = convertedBreeds.get(0);

        assertThat(convertedBreed.getId()).isEqualTo(sampleBreedDto.getId());
        assertThat(convertedBreed.getName()).isEqualTo(sampleBreedDto.getName());
        assertThat(convertedBreed.getTemperament()).isEqualTo(sampleBreedDto.getTemperament());
        assertThat(convertedBreed.getOrigin()).isEqualTo(sampleBreedDto.getOrigin());
        assertThat(convertedBreed.getDescription()).isEqualTo(sampleBreedDto.getDescription());
    }
}
