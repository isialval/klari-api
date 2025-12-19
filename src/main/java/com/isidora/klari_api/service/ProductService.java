package com.isidora.klari_api.service;

import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;

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
        return productRepository.findById(id).orElse(null);
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

    // búsquedas específicas

    public List<Product> findByCategory(ProductCategory category) {
        return productRepository.findByCategory(category);
    }

    public List<Product> findByApplicationTime(ProductApplicationTime applicationTime) {
        return productRepository.findByApplicationTime(applicationTime);
    }

    public List<Product> findByBrand(String brand) {
        return productRepository.findByBrand(brand);
    }

    public List<Product> search(String query, ProductCategory category) {
        boolean hasQuery = query != null && !query.trim().isEmpty();
        boolean hasCategory = category != null;

        if (hasQuery && hasCategory) {
            return productRepository.findByQueryAndCategory(query.trim(), category);
        }

        if (hasCategory) {
            return productRepository.findByCategory(category);
        }

        if (hasQuery) {
            return productRepository.findByNameContainingIgnoreCaseOrBrandContainingIgnoreCase(query, query);
        }

        return productRepository.findAll();
    }

    // Para rutinas

    public List<Product> findForRoutine(
            ProductCategory category,
            ProductApplicationTime time,
            SkinType skinType,
            Set<Goal> goals) {

        List<Product> results;

        // Prioridad 1: category + time + skinType + goals
        results = productRepository.findByCategoryAndTimeAndSkinTypeAndGoals(category, time, skinType, goals);
        if (!results.isEmpty()) {
            return results;
        }

        // Prioridad 2: category + time + skinType
        results = productRepository.findByCategoryAndTimeAndSkinType(category, time, skinType);
        if (!results.isEmpty()) {
            return results;
        }

        // Prioridad 3: category + time + goals
        results = productRepository.findByCategoryAndTimeAndGoals(category, time, goals);
        if (!results.isEmpty()) {
            return results;
        }

        // Prioridad 4: category + time (siempre devuelve algo)
        return productRepository.findByCategoryAndTime(category, time);
    }
}
