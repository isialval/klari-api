package com.isidora.klari_api.model;

import java.util.HashSet;
import java.util.Set;

import com.isidora.klari_api.model.enums.Goal;
import com.isidora.klari_api.model.enums.ProductApplicationTime;
import com.isidora.klari_api.model.enums.ProductCategory;
import com.isidora.klari_api.model.enums.SkinType;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@Entity
@Table(name = "products")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String brand;

    @Column(nullable = false)
    private String imageUrl;

    @Column
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProductCategory category;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProductApplicationTime applicationTime;

    @ElementCollection
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Set<Goal> goals = new HashSet<>();

    @ElementCollection
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Set<SkinType> skinTypes = new HashSet<>();
}
