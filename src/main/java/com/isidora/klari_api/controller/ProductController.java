package com.isidora.klari_api.controller;

import java.util.List;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.isidora.klari_api.dto.ProductSummaryDTO;
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

    @GetMapping("/{id}/summary")
    public ResponseEntity<ProductSummaryDTO> findSummaryById(@PathVariable Long id) {
        return ResponseEntity.ok(productService.findSummaryById(id));
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

    // Búsquedas específicas - PAGINADAS

    @GetMapping("/category/{category}")
    public ResponseEntity<Page<Product>> findByCategory(
            @PathVariable ProductCategory category,
            @PageableDefault(size = 20, sort = "id", direction = Sort.Direction.ASC) Pageable pageable) {
        return ResponseEntity.ok(productService.findByCategory(category, pageable));
    }

    @GetMapping("/brand/{brand}")
    public ResponseEntity<Page<Product>> findByBrand(
            @PathVariable String brand,
            @PageableDefault(size = 20, sort = "id", direction = Sort.Direction.ASC) Pageable pageable) {
        return ResponseEntity.ok(productService.findByBrand(brand, pageable));
    }

    @GetMapping("/search")
    public ResponseEntity<Page<Product>> search(
            @RequestParam(value = "q", required = false) String query,
            @RequestParam(value = "category", required = false) ProductCategory category,
            @PageableDefault(size = 20, sort = "id", direction = Sort.Direction.ASC) Pageable pageable) {
        return ResponseEntity.ok(productService.search(query, category, pageable));
    }

    // Recomendaciones para rutinas - PAGINADAS
    @GetMapping("/routine/recommend")
    public ResponseEntity<Page<ProductSummaryDTO>> recommendForRoutine(
            @RequestParam ProductCategory category,
            @RequestParam ProductApplicationTime time,
            @RequestParam SkinType skinType,
            @RequestParam Set<Goal> goals,
            @PageableDefault(size = 10, sort = "id", direction = Sort.Direction.ASC) Pageable pageable) {
        return ResponseEntity.ok(
                productService.findForRoutine(category, time, skinType, goals, pageable));
    }

    // Recomendaciones simples (sin paginación, con límite fijo)
    @GetMapping("/routine/recommend/simple")
    public ResponseEntity<List<ProductSummaryDTO>> recommendForRoutineSimple(
            @RequestParam ProductCategory category,
            @RequestParam ProductApplicationTime time,
            @RequestParam SkinType skinType,
            @RequestParam Set<Goal> goals,
            @RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok(
                productService.findForRoutineSimple(category, time, skinType, goals, Math.min(limit, 50)));
    }

    @GetMapping("/summary")
    public ResponseEntity<Page<ProductSummaryDTO>> findAllSummary(
            @PageableDefault(size = 20, sort = "id", direction = Sort.Direction.ASC) Pageable pageable) {
        return ResponseEntity.ok(productService.findAllSummary(pageable));
    }
}