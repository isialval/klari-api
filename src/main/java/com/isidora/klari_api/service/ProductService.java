package com.isidora.klari_api.service;

import java.util.List;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.isidora.klari_api.dto.ProductSummaryDTO;
import com.isidora.klari_api.model.Product;
import com.isidora.klari_api.model.enums.Goal;
import com.isidora.klari_api.model.enums.ProductApplicationTime;
import com.isidora.klari_api.model.enums.ProductCategory;
import com.isidora.klari_api.model.enums.SkinType;
import com.isidora.klari_api.repository.ProductRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    public Product create(Product product) {
        return productRepository.save(product);
    }

    public List<Product> findAll() {
        return productRepository.findAll();
    }

    public Product findById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Producto no encontrado"));
    }

    public Product update(Long id, Product productDetails) {
        Product product = findById(id);

        product.setName(productDetails.getName());
        product.setBrand(productDetails.getBrand());
        product.setImageUrl(productDetails.getImageUrl());
        product.setIngredients(productDetails.getIngredients());
        product.setDescription(productDetails.getDescription());
        product.setCategory(productDetails.getCategory());
        product.setApplicationTime(productDetails.getApplicationTime());
        product.setSkinTypes(productDetails.getSkinTypes());
        product.setGoals(productDetails.getGoals());

        return productRepository.save(product);
    }

    public void delete(Long id) {
        Product product = findById(id);
        productRepository.delete(product);
    }

    public Page<Product> findByCategory(ProductCategory category, Pageable pageable) {
        return productRepository.findByCategory(category, pageable);
    }

    public Page<Product> findByApplicationTime(ProductApplicationTime applicationTime, Pageable pageable) {
        return productRepository.findByApplicationTime(applicationTime, pageable);
    }

    public Page<Product> findByBrand(String brand, Pageable pageable) {
        return productRepository.findByBrandIgnoreCase(brand, pageable);
    }

    public Page<Product> search(String query, ProductCategory category, Pageable pageable) {
        boolean hasQuery = query != null && !query.trim().isEmpty();
        boolean hasCategory = category != null;

        if (hasQuery && hasCategory) {
            return productRepository.findByQueryAndCategory(query.trim(), category, pageable);
        }

        if (hasCategory) {
            return productRepository.findByCategory(category, pageable);
        }

        if (hasQuery) {
            return productRepository.findByNameContainingIgnoreCaseOrBrandContainingIgnoreCase(query, query, pageable);
        }

        return productRepository.findAll(pageable);
    }

    public Page<ProductSummaryDTO> findForRoutine(
            ProductCategory category,
            ProductApplicationTime time,
            SkinType skinType,
            Set<Goal> goals,
            Pageable pageable) {

        Page<ProductSummaryDTO> results = productRepository.findRecommendationsFull(
                category, time, skinType, goals, pageable);

        if (results.hasContent()) {
            return results;
        }

        results = productRepository.findRecommendationsBySkinType(category, time, skinType, pageable);

        if (results.hasContent()) {
            return results;
        }

        results = productRepository.findRecommendationsByGoals(category, time, goals, pageable);

        if (results.hasContent()) {
            return results;
        }

        results = productRepository.findRecommendationsByCategoryAndTime(category, time, pageable);

        if (results.hasContent()) {
            return results;
        }

        return productRepository.findSummaryByCategory(category, pageable);
    }

    public List<ProductSummaryDTO> findForRoutineSimple(
            ProductCategory category,
            ProductApplicationTime time,
            SkinType skinType,
            Set<Goal> goals,
            int limit) {

        Pageable pageable = PageRequest.of(0, limit);
        return findForRoutine(category, time, skinType, goals, pageable).getContent();
    }

    public Page<ProductSummaryDTO> findAllSummary(Pageable pageable) {
        return productRepository.findAllSummary(pageable);
    }

    public ProductSummaryDTO findSummaryById(Long id) {
        return productRepository.findSummaryById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Producto no encontrado"));
    }
}