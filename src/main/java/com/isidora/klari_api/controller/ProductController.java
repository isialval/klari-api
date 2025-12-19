package com.isidora.klari_api.controller;

import java.util.List;
import java.util.Set;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.isidora.klari_api.model.Product;
import com.isidora.klari_api.model.enums.Goal;
import com.isidora.klari_api.model.enums.ProductApplicationTime;
import com.isidora.klari_api.model.enums.ProductCategory;
import com.isidora.klari_api.model.enums.SkinType;
import com.isidora.klari_api.service.ProductService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @PostMapping
    public ResponseEntity<Product> create(@Valid @RequestBody Product product) {
        Product created = productService.create(product);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping
    public ResponseEntity<List<Product>> findAll() {
        return ResponseEntity.ok(productService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Product> findById(@PathVariable Long id) {
        return ResponseEntity.ok(productService.findById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Product> update(@PathVariable Long id, @Valid @RequestBody Product productDetails) {
        return ResponseEntity.ok(productService.update(id, productDetails));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        productService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/bulk")
    public ResponseEntity<List<Product>> createBulk(@Valid @RequestBody List<Product> products) {
        List<Product> saved = products.stream()
                .map(productService::create)
                .toList();
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    // búsquedas específicas

    @GetMapping("/category/{category}")
    public ResponseEntity<List<Product>> findByCategory(@PathVariable ProductCategory category) {
        return ResponseEntity.ok(productService.findByCategory(category));
    }

    @GetMapping("/brand/{brand}")
    public ResponseEntity<List<Product>> findByBrand(@PathVariable String brand) {
        return ResponseEntity.ok(productService.findByBrand(brand));
    }

    @GetMapping("/search")
    public ResponseEntity<List<Product>> search(
            @RequestParam(value = "q", required = false) String query,
            @RequestParam(value = "category", required = false) ProductCategory category) {

        return ResponseEntity.ok(productService.search(query, category));
    }

    // Recomendaciones para rutinas
    @GetMapping("/routine/recommend")
    public ResponseEntity<List<Product>> recommendForRoutine(
            @RequestParam ProductCategory category,
            @RequestParam ProductApplicationTime time,
            @RequestParam SkinType skinType,
            @RequestParam Set<Goal> goals) {

        return ResponseEntity.ok(
                productService.findForRoutine(category, time, skinType, goals));
    }
}
