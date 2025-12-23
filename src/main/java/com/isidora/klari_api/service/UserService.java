package com.isidora.klari_api.service;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.isidora.klari_api.model.Product;
import com.isidora.klari_api.model.Routine;
import com.isidora.klari_api.model.User;
import com.isidora.klari_api.model.enums.Goal;
import com.isidora.klari_api.model.enums.SkinType;
import com.isidora.klari_api.repository.ProductRepository;
import com.isidora.klari_api.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    public User create(User user) {
        return userRepository.save(user);
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public User findById(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    }

    public Optional<User> findbyUserName(String userName) {
        return userRepository.findByUsername(userName);
    }

    // favoritos e inventario

    @Transactional(readOnly = true)
    public Set<Product> getFavorites(Long id) {
        User user = findById(id);
        return user.getFavorites();
    }

    @Transactional(readOnly = true)
    public Set<Product> getInventory(Long id) {
        User user = findById(id);
        return user.getInventory();
    }

    @Transactional
    public void addFavorite(Long userId, Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        user.getFavorites().add(product);
    }

    @Transactional
    public void addToInventory(Long userId, Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        user.getInventory().add(product);
    }

    @Transactional
    public void removeFavorite(Long userId, Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        user.getFavorites().remove(product);
    }

    @Transactional
    public void removeFromInventory(Long userId, Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        user.getInventory().remove(product);
    }

    // Tipo de piel, objetivos y rutinas

    public SkinType getSkinType(Long id) {
        return findById(id).getSkinType();
    }

    @Transactional(readOnly = true)
    public Set<Goal> getGoals(Long id) {
        return findById(id).getGoals();
    }

    @Transactional(readOnly = true)
    public List<Routine> getRoutines(Long id) {
        return findById(id).getRoutines();
    }

    @Transactional
    public void setSkinType(Long userId, SkinType skinType) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        user.setSkinType(skinType);
    }

    @Transactional
    public void addGoal(Long userId, Goal goal) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        user.getGoals().add(goal);
    }

    @Transactional
    public void removeGoal(Long userId, Goal goal) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        user.getGoals().remove(goal);
    }
}
