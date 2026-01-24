package com.isidora.klari_api.repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.isidora.klari_api.dto.ProductSummaryDTO;
import com.isidora.klari_api.model.Product;
import com.isidora.klari_api.model.enums.Goal;
import com.isidora.klari_api.model.enums.ProductApplicationTime;
import com.isidora.klari_api.model.enums.ProductCategory;
import com.isidora.klari_api.model.enums.SkinType;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

        Page<Product> findByCategory(ProductCategory category, Pageable pageable);

        Page<Product> findByApplicationTime(ProductApplicationTime applicationTime, Pageable pageable);

        Page<Product> findByBrandIgnoreCase(String brand, Pageable pageable);

        Page<Product> findByNameContainingIgnoreCaseOrBrandContainingIgnoreCase(
                        String name, String brand, Pageable pageable);

        @Query("SELECT p FROM Product p WHERE " +
                        "(LOWER(p.name) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
                        "LOWER(p.brand) LIKE LOWER(CONCAT('%', :query, '%'))) " +
                        "AND p.category = :category")
        Page<Product> findByQueryAndCategory(
                        @Param("query") String query,
                        @Param("category") ProductCategory category,
                        Pageable pageable);

        @Query("""
                        SELECT new com.isidora.klari_api.dto.ProductSummaryDTO(
                            p.id, p.name, p.brand, p.imageUrl, p.category
                        )
                        FROM Product p
                        """)
        Page<ProductSummaryDTO> findAllSummary(Pageable pageable);

        @Query("""
                        SELECT new com.isidora.klari_api.dto.ProductSummaryDTO(
                            p.id, p.name, p.brand, p.imageUrl, p.category
                        )
                        FROM Product p
                        WHERE p.category = :category
                        """)
        Page<ProductSummaryDTO> findSummaryByCategory(
                        @Param("category") ProductCategory category,
                        Pageable pageable);

        @Query("""
                        SELECT new com.isidora.klari_api.dto.ProductSummaryDTO(
                            p.id, p.name, p.brand, p.imageUrl, p.category
                        )
                        FROM Product p
                        WHERE p.id = :id
                        """)
        Optional<ProductSummaryDTO> findSummaryById(@Param("id") Long id);

        @Query("""
                        SELECT new com.isidora.klari_api.dto.ProductSummaryDTO(
                            p.id, p.name, p.brand, p.imageUrl, p.category
                        )
                        FROM Product p
                        WHERE p.category = :category
                        AND (p.applicationTime = :time OR p.applicationTime = com.isidora.klari_api.model.enums.ProductApplicationTime.AMBOS)
                        AND :skinType MEMBER OF p.skinTypes
                        AND EXISTS (SELECT g FROM p.goals g WHERE g IN :goals)
                        """)
        Page<ProductSummaryDTO> findRecommendationsFull(
                        @Param("category") ProductCategory category,
                        @Param("time") ProductApplicationTime time,
                        @Param("skinType") SkinType skinType,
                        @Param("goals") Set<Goal> goals,
                        Pageable pageable);

        @Query("""
                        SELECT new com.isidora.klari_api.dto.ProductSummaryDTO(
                            p.id, p.name, p.brand, p.imageUrl, p.category
                        )
                        FROM Product p
                        WHERE p.category = :category
                        AND (p.applicationTime = :time OR p.applicationTime = com.isidora.klari_api.model.enums.ProductApplicationTime.AMBOS)
                        AND :skinType MEMBER OF p.skinTypes
                        """)
        Page<ProductSummaryDTO> findRecommendationsBySkinType(
                        @Param("category") ProductCategory category,
                        @Param("time") ProductApplicationTime time,
                        @Param("skinType") SkinType skinType,
                        Pageable pageable);

        @Query("""
                        SELECT new com.isidora.klari_api.dto.ProductSummaryDTO(
                            p.id, p.name, p.brand, p.imageUrl, p.category
                        )
                        FROM Product p
                        WHERE p.category = :category
                        AND (p.applicationTime = :time OR p.applicationTime = com.isidora.klari_api.model.enums.ProductApplicationTime.AMBOS)
                        AND EXISTS (SELECT g FROM p.goals g WHERE g IN :goals)
                        """)
        Page<ProductSummaryDTO> findRecommendationsByGoals(
                        @Param("category") ProductCategory category,
                        @Param("time") ProductApplicationTime time,
                        @Param("goals") Set<Goal> goals,
                        Pageable pageable);

        @Query("""
                        SELECT new com.isidora.klari_api.dto.ProductSummaryDTO(
                            p.id, p.name, p.brand, p.imageUrl, p.category
                        )
                        FROM Product p
                        WHERE p.category = :category
                        AND (p.applicationTime = :time OR p.applicationTime = com.isidora.klari_api.model.enums.ProductApplicationTime.AMBOS)
                        """)
        Page<ProductSummaryDTO> findRecommendationsByCategoryAndTime(
                        @Param("category") ProductCategory category,
                        @Param("time") ProductApplicationTime time,
                        Pageable pageable);

        List<Product> findByCategory(ProductCategory category);

        List<Product> findByBrand(String brand);
}