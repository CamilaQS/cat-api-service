package com.itau.cat_api.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.hibernate.validator.constraints.URL;
import lombok.*;

import java.io.Serializable;
import java.util.List;

@Entity
@Table(name = "breeds")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Breed implements Serializable {

    @Id
    @Column(name = "breed_id", length = 10, nullable = false, unique = true)
    @EqualsAndHashCode.Include
    private String id;

    @NotBlank
    @Size(max = 100)
    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "temperament", columnDefinition = "TEXT")
    private String temperament;

    @Size(max = 100)
    @Column(name = "origin", length = 100)
    private String origin;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "life_span", length = 50)
    private String lifeSpan;

    @URL
    @Column(name = "wikipedia_url")
    private String wikipediaUrl;

    @Column(name = "reference_image_id", length = 50)
    private String referenceImageId;

    @OneToMany(mappedBy = "breed", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Image> images;

}
