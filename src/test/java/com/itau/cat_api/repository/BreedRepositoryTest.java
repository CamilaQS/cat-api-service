package com.itau.cat_api.repository;

import com.itau.cat_api.model.entity.Breed;
import com.itau.cat_api.repository.BreedRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Testes de integração para BreedRepository
 *
 * Esta classe usa @DataJpaTest para testar apenas a camada de persistência,
 * configurando automaticamente um banco de dados em memória (H2) e
 * fornecendo um TestEntityManager para manipular dados de teste.
 */

public class BreedRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private BreedRepository breedRepository;

    private Breed abyssinianBreed;
    private Breed bengalBreed;
    private Breed persianBreed;

    @BeforeEach
    void setUp() {
        // Arrange: Preparar dados de teste no banco em memória
        abyssinianBreed = new Breed();
        abyssinianBreed.setId("abys");
        abyssinianBreed.setName("Abyssinian");
        abyssinianBreed.setTemperament("Active, Energetic, Independent, Intelligent, Gentle");
        abyssinianBreed.setOrigin("Egypt");
        abyssinianBreed.setDescription("The Abyssinian is easy to care for, and a joy to have in your home.");

        bengalBreed = new Breed();
        bengalBreed.setId("beng");
        bengalBreed.setName("Bengal");
        bengalBreed.setTemperament("Alert, Agile, Energetic, Demanding, Intelligent");
        bengalBreed.setOrigin("United States");
        bengalBreed.setDescription("Bengals are a lot of fun to live with, but they're definitely not the cat for everyone.");

        persianBreed = new Breed();
        persianBreed.setId("pers");
        persianBreed.setName("Persian");
        persianBreed.setTemperament("Affectionate, Docile, Quiet, Gentle");
        persianBreed.setOrigin("Iran");
        persianBreed.setDescription("The Persian is a longhaired breed of cat characterized by its round face and short muzzle.");

        // Persistir os dados de teste
        entityManager.persistAndFlush(abyssinianBreed);
        entityManager.persistAndFlush(bengalBreed);
        entityManager.persistAndFlush(persianBreed);
    }


}
