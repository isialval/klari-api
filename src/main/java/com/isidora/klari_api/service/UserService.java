package com.isidora.klari_api.service;

import java.util.List;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.isidora.klari_api.dto.ProductSummaryDTO;
import com.isidora.klari_api.model.Product;
import com.isidora.klari_api.model.Routine;
import com.isidora.klari_api.model.User;
import com.isidora.klari_api.model.enums.Goal;
import com.isidora.klari_api.model.enums.ProductCategory;
import com.isidora.klari_api.model.enums.SkinType;
import com.isidora.klari_api.repository.ProductRepository;
import com.isidora.klari_api.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    @Transactional(readOnly = true)
    public User findById(Long id) {
        assertSelf(id);
        return userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));
    }

    @Transactional
    public void addFavorite(Long userId, Long productId) {
        assertSelf(userId);
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Producto no encontrado"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));
        user.getFavorites().add(product);
    }

    @Transactional
    public void addToInventory(Long userId, Long productId) {
        assertSelf(userId);
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Producto no encontrado"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));
        user.getInventory().add(product);
    }

    @Transactional
    public void removeFavorite(Long userId, Long productId) {
        assertSelf(userId);
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Producto no encontrado"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));
        user.getFavorites().remove(product);
    }

    @Transactional
    public void removeFromInventory(Long userId, Long productId) {
        assertSelf(userId);
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Producto no encontrado"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));
        user.getInventory().remove(product);
    }

    @Transactional(readOnly = true)
    public SkinType getSkinType(Long id) {
        assertSelf(id);
        return findById(id).getSkinType();
    }

    @Transactional(readOnly = true)
    public Set<Goal> getGoals(Long id) {
        assertSelf(id);
        return findById(id).getGoals();
    }

    @Transactional(readOnly = true)
    public List<Routine> getRoutines(Long id) {
        assertSelf(id);
        return findById(id).getRoutines();
    }

    @Transactional
    public void setSkinType(Long userId, SkinType skinType) {
        assertSelf(userId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));
        user.setSkinType(skinType);
    }

    @Transactional
    public void addGoal(Long userId, Goal goal) {
        assertSelf(userId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));
        user.getGoals().add(goal);
    }

    @Transactional
    public void removeGoal(Long userId, Goal goal) {
        assertSelf(userId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));
        user.getGoals().remove(goal);
    }

    private Long authUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "No autenticado");
        }
        return (Long) auth.getPrincipal();
    }

    private void assertSelf(Long pathUserId) {
        Long me = authUserId();
        if (!me.equals(pathUserId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No autorizado");
        }
    }

    @Transactional(readOnly = true)
    public Page<ProductSummaryDTO> getFavoriteSummaries(Long id, Pageable pageable) {
        assertSelf(id);
        return userRepository.findFavoriteSummaries(id, pageable);
    }

    @Transactional(readOnly = true)
    public Page<ProductSummaryDTO> getInventorySummaries(Long id, Pageable pageable) {
        assertSelf(id);
        return userRepository.findInventorySummaries(id, pageable);
    }

    @Transactional(readOnly = true)
    public Page<ProductSummaryDTO> getFavoriteSummariesByCategory(Long id, ProductCategory category,
            Pageable pageable) {
        assertSelf(id);
        return userRepository.findFavoriteSummariesByCategory(id, category, pageable);
    }

    @Transactional(readOnly = true)
    public Page<ProductSummaryDTO> getInventorySummariesByCategory(Long id, ProductCategory category,
            Pageable pageable) {
        assertSelf(id);
        return userRepository.findInventorySummariesByCategory(id, category, pageable);
    }

    @Transactional(readOnly = true)
    public boolean isFavorite(Long userId, Long productId) {
        assertSelf(userId);
        return userRepository.existsFavorite(userId, productId);
    }

    @Transactional(readOnly = true)
    public boolean isInInventory(Long userId, Long productId) {
        assertSelf(userId);
        return userRepository.existsInInventory(userId, productId);
    }
}