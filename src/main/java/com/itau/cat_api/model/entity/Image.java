package com.itau.cat_api.model.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "images")
@Getter
@Setter
@Builder
public class Image {

    @Id
    @Column(name = "id", nullable = false, columnDefinition = "TEXT")
    private String id;

    @Column(name = "url", nullable = false, columnDefinition = "TEXT")
    private String url;

    @Column(name = "width")
    private Integer width;

    @Column(name = "height")
    private Integer height;

    @Enumerated(EnumType.STRING)
    @Column(name = "category")
    private ImageCategory category;

    @Column(name = "breed")
    private Breed breed;


    public enum ImageCategory {
        BREED, HATS, SUNGLASSES
    }


}
