package com.isidora.klari_api.repository;

import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.isidora.klari_api.model.Product;
import com.isidora.klari_api.model.enums.Goal;
import com.isidora.klari_api.model.enums.ProductApplicationTime;
import com.isidora.klari_api.model.enums.ProductCategory;
import com.isidora.klari_api.model.enums.SkinType;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    List<Product> findByCategory(ProductCategory category);

    List<Product> findByApplicationTime(ProductApplicationTime applicationTime);

    List<Product> findByBrand(String brand);

    List<Product> findByNameContainingIgnoreCaseOrBrandContainingIgnoreCase(String name, String brand);

    // Búsqueda combinada de texto y categoría
    @Query("SELECT p FROM Product p WHERE " +
            "(LOWER(p.name) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(p.brand) LIKE LOWER(CONCAT('%', :query, '%'))) " +
            "AND p.category = :category")
    List<Product> findByQueryAndCategory(
            @Param("query") String query,
            @Param("category") ProductCategory category);

    // Productos compatibles con un tipo de piel
    @Query("SELECT p FROM Product p WHERE :skinType MEMBER OF p.skinTypes")
    List<Product> findBySkinType(@Param("skinType") SkinType skinType);

    // Productos que ayudan con un goal específico
    @Query("SELECT p FROM Product p WHERE :goal MEMBER OF p.goals")
    List<Product> findByGoal(@Param("goal") Goal goal);

    // En ProductRepository.java (igual que antes)

    // Prioridad 1 recomendaciones
    @Query("SELECT p FROM Product p WHERE " +
            "p.category = :category " +
            "AND (p.applicationTime = :time OR p.applicationTime = 'BOTH') " +
            "AND :skinType MEMBER OF p.skinTypes " +
            "AND EXISTS (SELECT g FROM p.goals g WHERE g IN :goals)")
    List<Product> findByCategoryAndTimeAndSkinTypeAndGoals(
            @Param("category") ProductCategory category,
            @Param("time") ProductApplicationTime time,
            @Param("skinType") SkinType skinType,
            @Param("goals") Set<Goal> goals);

    // Prioridad 2 recomendaciones
    @Query("SELECT p FROM Product p WHERE " +
            "p.category = :category " +
            "AND (p.applicationTime = :time OR p.applicationTime = 'BOTH') " +
            "AND :skinType MEMBER OF p.skinTypes")
    List<Product> findByCategoryAndTimeAndSkinType(
            @Param("category") ProductCategory category,
            @Param("time") ProductApplicationTime time,
            @Param("skinType") SkinType skinType);

    // Prioridad 3 recomendaciones
    @Query("SELECT p FROM Product p WHERE " +
            "p.category = :category " +
            "AND (p.applicationTime = :time OR p.applicationTime = 'BOTH') " +
            "AND EXISTS (SELECT g FROM p.goals g WHERE g IN :goals)")
    List<Product> findByCategoryAndTimeAndGoals(
            @Param("category") ProductCategory category,
            @Param("time") ProductApplicationTime time,
            @Param("goals") Set<Goal> goals);

    // Prioridad 4 recomendaciones
    @Query("SELECT p FROM Product p WHERE " +
            "p.category = :category " +
            "AND (p.applicationTime = :time OR p.applicationTime = 'BOTH')")
    List<Product> findByCategoryAndTime(
            @Param("category") ProductCategory category,
            @Param("time") ProductApplicationTime time);

}
