package com.isidora.klari_api.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.isidora.klari_api.model.Product;
import com.isidora.klari_api.model.Routine;
import com.isidora.klari_api.model.User;
import com.isidora.klari_api.model.enums.ProductApplicationTime;
import com.isidora.klari_api.model.enums.ProductCategory;
import com.isidora.klari_api.model.enums.RoutineType;
import com.isidora.klari_api.repository.ProductRepository;
import com.isidora.klari_api.repository.RoutineRepository;
import com.isidora.klari_api.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RoutineService {

    private final RoutineRepository routineRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    private final ProductService productService;

    public Routine create(Routine routine) {
        return routineRepository.save(routine);
    }

    public Optional<Routine> findById(Long id) {
        return routineRepository.findById(id);
    }

    public List<Routine> findByUserId(Long id) {
        return routineRepository.findByUserId(id);
    }

    public Optional<Routine> findActiveDayRoutine(Long userId) {
        return routineRepository.findActiveRoutine(RoutineType.DIA, userId);
    }

    public List<Routine> findInactiveDayRoutines(Long userId) {
        return routineRepository.findInactiveRoutines(RoutineType.DIA, userId);
    }

    public Optional<Routine> findActiveNightRoutine(Long userId) {
        return routineRepository.findActiveRoutine(RoutineType.NOCHE, userId);
    }

    public List<Routine> findInactiveNightRoutines(Long userId) {
        return routineRepository.findInactiveRoutines(RoutineType.NOCHE, userId);
    }

    @Transactional
    public void remove(Long routineId) {
        Routine routine = routineRepository.findById(routineId).orElse(null);

        if (routine != null) {
            routine.getProducts().clear();
            routineRepository.delete(routine);
        }
    }

    // añadir y quitar productos a rutina
    @Transactional
    public void addProduct(Long routineId, Long productId) {
        Routine routine = routineRepository.findById(routineId)
                .orElseThrow(() -> new RuntimeException("Rutina no encontrada"));

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

        routine.getProducts().removeIf(p -> p.getCategory().equals(product.getCategory()));

        routine.getProducts().add(product);

        routineRepository.save(routine);
    }

    @Transactional
    public void removeProduct(Long routineId, Long productId) {
        Routine routine = routineRepository.findById(routineId)
                .orElseThrow(() -> new RuntimeException("Rutina no encontrada"));

        routine.getProducts().removeIf(p -> p.getId().equals(productId));

        routineRepository.save(routine);
    }

    // Rutinas iniciales (con productos básicos pre-seleccionados)
    @Transactional
    public Routine createInitialDayRoutine(Long userId) {

        Optional<Routine> existingRoutine = routineRepository.findActiveRoutine(RoutineType.DIA, userId);
        if (existingRoutine.isPresent()) {
            throw new RuntimeException("Ya existe una rutina de día activa");
        }
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Routine routine = new Routine();
        routine.setUser(user);
        routine.setRoutineType(RoutineType.DIA);
        routine.setActive(true);

        addProductIfExists(routine, ProductCategory.LIMPIADOR, ProductApplicationTime.DIA, user);
        addProductIfExists(routine, ProductCategory.SERUM, ProductApplicationTime.DIA, user);
        addProductIfExists(routine, ProductCategory.HIDRATANTE, ProductApplicationTime.DIA, user);
        addProductIfExists(routine, ProductCategory.PROTECTOR_SOLAR, ProductApplicationTime.DIA, user);

        return routineRepository.save(routine);

    }

    @Transactional
    public Routine createInitialNightRoutine(Long userId) {

        Optional<Routine> existingRoutine = routineRepository.findActiveRoutine(RoutineType.NOCHE, userId);
        if (existingRoutine.isPresent()) {
            throw new RuntimeException("Ya existe una rutina de noche activa");
        }
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Routine routine = new Routine();
        routine.setUser(user);
        routine.setRoutineType(RoutineType.NOCHE);
        routine.setActive(true);

        addProductIfExists(routine, ProductCategory.LIMPIADOR, ProductApplicationTime.NOCHE, user);
        addProductIfExists(routine, ProductCategory.SERUM, ProductApplicationTime.NOCHE, user);
        addProductIfExists(routine, ProductCategory.HIDRATANTE, ProductApplicationTime.NOCHE, user);

        return routineRepository.save(routine);
    }

    private void addProductIfExists(Routine routine, ProductCategory category,
            ProductApplicationTime time, User user) {
        List<Product> products = productService.findForRoutine(
                category, time, user.getSkinType(), user.getGoals());

        if (!products.isEmpty()) {
            routine.getProducts().add(products.get(0));
        }
    }

    // activar y desactivar rutinas
    @Transactional
    public void deactivate(Long routineId) {
        Routine routine = routineRepository.findById(routineId)
                .orElseThrow(() -> new RuntimeException("Rutina no encontrada"));

        routine.setActive(false);
        routineRepository.save(routine);
    }

    @Transactional
    public void activate(Long routineId) {
        Routine routine = routineRepository.findById(routineId)
                .orElseThrow(() -> new RuntimeException("Rutina no encontrada"));

        routine.setActive(true);
        routineRepository.save(routine);
    }

}
