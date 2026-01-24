package com.isidora.klari_api.dto;

import com.isidora.klari_api.model.enums.ProductCategory;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ProductSummaryDTO {
    private Long id;
    private String name;
    private String brand;
    private String imageUrl;
    private ProductCategory category;
}
