package com.itau.cat_api.repository;

import com.itau.cat_api.model.entity.Breed;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BreedRepository extends JpaRepository<Breed, String> {

    @Query("SELECT b FROM Breed b WHERE LOWER(b.temperament) LIKE LOWER(CONCAT('%', :temperament, '%'))")
    List<Breed> findByTemperamentContainingIgnoreCase(@Param("temperament") String temperament);

    @Query("SELECT b FROM Breed b WHERE LOWER(b.origin) = LOWER(:origin)")
    List<Breed> findByOriginIgnoreCase(@Param("origin") String origin);

    @Query("SELECT DISTINCT b.origin FROM Breed b WHERE b.origin IS NOT NULL ORDER BY b.origin")
    List<String> findAllDistinctOrigins();

    @Query("SELECT DISTINCT b.temperament FROM Breed b WHERE b.temperament IS NOT NULL")
    List<String> findAllDistinctTemperaments();

}

