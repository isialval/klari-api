package com.isidora.klari_api.service;

import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.isidora.klari_api.model.Product;
import com.isidora.klari_api.model.Routine;
import com.isidora.klari_api.model.User;
import com.isidora.klari_api.model.enums.ProductApplicationTime;
import com.isidora.klari_api.model.enums.ProductCategory;
import com.isidora.klari_api.model.enums.RoutineType;
import com.isidora.klari_api.repository.ProductRepository;
import com.isidora.klari_api.repository.RoutineRepository;
import com.isidora.klari_api.repository.UserRepository;
import org.springframework.security.core.Authentication;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RoutineService {

    private final RoutineRepository routineRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    private final ProductService productService;

    public Routine create(Routine routine) {
        if (routine.getUser() == null || routine.getUser().getId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La rutina debe tener user");
        }
        assertSelf(routine.getUser().getId());
        return routineRepository.save(routine);
    }

    public Optional<Routine> findById(Long id) {
        Optional<Routine> routine = routineRepository.findById(id);
        routine.ifPresent(this::assertOwner);
        return routine;
    }

    public List<Routine> findByUserId(Long userId) {
        assertSelf(userId);
        return routineRepository.findByUserId(userId);
    }

    public Optional<Routine> findActiveDayRoutine(Long userId) {
        assertSelf(userId);
        return routineRepository.findActiveRoutine(RoutineType.DIA, userId);
    }

    public List<Routine> findInactiveDayRoutines(Long userId) {
        assertSelf(userId);
        return routineRepository.findInactiveRoutines(RoutineType.DIA, userId);
    }

    public Optional<Routine> findActiveNightRoutine(Long userId) {
        assertSelf(userId);
        return routineRepository.findActiveRoutine(RoutineType.NOCHE, userId);
    }

    public List<Routine> findInactiveNightRoutines(Long userId) {
        assertSelf(userId);
        return routineRepository.findInactiveRoutines(RoutineType.NOCHE, userId);
    }

    @Transactional
    public void remove(Long routineId) {
        Routine routine = findRoutineOrThrow(routineId);
        assertOwner(routine);

        routine.getProducts().clear();
        routineRepository.delete(routine);
    }

    // Añadir y quitar productos a rutina existente
    @Transactional
    public void addProduct(Long routineId, Long productId) {
        Routine routine = findRoutineOrThrow(routineId);
        assertOwner(routine);

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

        routine.getProducts().removeIf(p -> p.getCategory().equals(product.getCategory()));

        routine.getProducts().add(product);

        routineRepository.save(routine);
    }

    @Transactional
    public void removeProduct(Long routineId, Long productId) {
        Routine routine = findRoutineOrThrow(routineId);
        assertOwner(routine);

        routine.getProducts().removeIf(p -> p.getId().equals(productId));

        routineRepository.save(routine);
    }

    // Rutinas iniciales (con productos básicos pre-seleccionados)
    @Transactional
    public Routine createInitialDayRoutine(Long userId) {
        assertSelf(userId);

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
        assertSelf(userId);

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

    // Activar y desactivar rutinas
    @Transactional
    public void deactivate(Long routineId) {
        Routine routine = findRoutineOrThrow(routineId);
        assertOwner(routine);

        routine.setActive(false);
        routineRepository.save(routine);
    }

    @Transactional
    public void activate(Long routineId) {
        Routine routine = findRoutineOrThrow(routineId);
        assertOwner(routine);

        routine.setActive(true);
        routineRepository.save(routine);
    }

    // helpers

    private Long authUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "No autenticado");
        }
        return (Long) auth.getPrincipal(); // JwtFilter -> principal=userId
    }

    private void assertSelf(Long pathUserId) {
        Long me = authUserId();
        if (!me.equals(pathUserId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No autorizado");
        }
    }

    private Routine findRoutineOrThrow(Long routineId) {
        return routineRepository.findById(routineId)
                .orElseThrow(() -> new RuntimeException("Rutina no encontrada"));
    }

    private void assertOwner(Routine routine) {
        Long me = authUserId();
        Long ownerId = routine.getUser().getId();
        if (!me.equals(ownerId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No autorizado");
        }
    }

}
