package com.itau.cat_api.model.dto;


import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import jakarta.persistence.Id;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class BreedDTO {

    private String id;
    private String name;
    private String temperament;
    private String origin;
    private String description;
    private String lifeSpan;
    private String wikipediaUrl;
    private String referenceImageId;

}
