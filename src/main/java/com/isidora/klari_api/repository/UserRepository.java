package com.isidora.klari_api.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.isidora.klari_api.dto.ProductSummaryDTO;
import com.isidora.klari_api.model.User;
import com.isidora.klari_api.model.enums.ProductCategory;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    // FAVORITOS - Paginado
    @Query("""
            SELECT new com.isidora.klari_api.dto.ProductSummaryDTO(
                p.id, p.name, p.brand, p.imageUrl, p.category
            )
            FROM User u
            JOIN u.favorites p
            WHERE u.id = :userId
            """)
    Page<ProductSummaryDTO> findFavoriteSummaries(@Param("userId") Long userId, Pageable pageable);

    // INVENTARIO - Paginado
    @Query("""
            SELECT new com.isidora.klari_api.dto.ProductSummaryDTO(
                p.id, p.name, p.brand, p.imageUrl, p.category
            )
            FROM User u
            JOIN u.inventory p
            WHERE u.id = :userId
            """)
    Page<ProductSummaryDTO> findInventorySummaries(@Param("userId") Long userId, Pageable pageable);

    // FAVORITOS filtrado por categoría - Paginado
    @Query("""
            SELECT new com.isidora.klari_api.dto.ProductSummaryDTO(
                p.id, p.name, p.brand, p.imageUrl, p.category
            )
            FROM User u
            JOIN u.favorites p
            WHERE u.id = :userId AND p.category = :category
            """)
    Page<ProductSummaryDTO> findFavoriteSummariesByCategory(
            @Param("userId") Long userId,
            @Param("category") ProductCategory category,
            Pageable pageable);

    // INVENTARIO filtrado por categoría - Paginado
    @Query("""
            SELECT new com.isidora.klari_api.dto.ProductSummaryDTO(
                p.id, p.name, p.brand, p.imageUrl, p.category
            )
            FROM User u
            JOIN u.inventory p
            WHERE u.id = :userId AND p.category = :category
            """)
    Page<ProductSummaryDTO> findInventorySummariesByCategory(
            @Param("userId") Long userId,
            @Param("category") ProductCategory category,
            Pageable pageable);

    // Verificar si existe en favoritos
    @Query("""
            SELECT CASE WHEN COUNT(p) > 0 THEN true ELSE false END
            FROM User u
            JOIN u.favorites p
            WHERE u.id = :userId AND p.id = :productId
            """)
    boolean existsFavorite(@Param("userId") Long userId, @Param("productId") Long productId);

    // Verificar si existe en inventario
    @Query("""
            SELECT CASE WHEN COUNT(p) > 0 THEN true ELSE false END
            FROM User u
            JOIN u.inventory p
            WHERE u.id = :userId AND p.id = :productId
            """)
    boolean existsInInventory(@Param("userId") Long userId, @Param("productId") Long productId);
}