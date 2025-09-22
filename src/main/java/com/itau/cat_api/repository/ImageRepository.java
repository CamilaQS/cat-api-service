package com.itau.cat_api.repository;

import com.itau.cat_api.model.entity.Image;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface ImageRepository extends JpaRepository<Image, String> {

    List<Image> findByCategory(Image.ImageCategory category);

    @Query("SELECT i FROM Image i WHERE i.breed.id = :breedId")
    List<Image> findByBreedId(@Param("breedId") String breedId);

    @Query("SELECT i FROM Image i WHERE i.category = :category ORDER BY i.id LIMIT :limit")
    List<Image> findByCategoryWithLimit(@Param("category") Image.ImageCategory category, @Param("limit") int limit);
}