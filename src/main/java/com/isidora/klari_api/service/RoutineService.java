package com.isidora.klari_api.service;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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
import com.isidora.klari_api.model.enums.ProductApplicationTime;
import com.isidora.klari_api.model.enums.ProductCategory;
import com.isidora.klari_api.model.enums.RoutineType;
import com.isidora.klari_api.model.enums.SkinType;
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

    @Transactional
    public void addProduct(Long routineId, Long productId) {
        Routine routine = findRoutineOrThrow(routineId);
        assertOwner(routine);

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Producto no encontrado"));

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

    @Transactional
    public Routine createInitialDayRoutine(Long userId) {
        assertSelf(userId);

        Optional<Routine> existingRoutine = routineRepository.findActiveRoutine(RoutineType.DIA, userId);
        if (existingRoutine.isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Ya existe una rutina de día activa");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));

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
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Ya existe una rutina de noche activa");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));

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

        SkinType skinType = user.getSkinType();
        Set<Goal> goals = user.getGoals();
        PageRequest pageRequest = PageRequest.of(0, 1);

        Page<ProductSummaryDTO> recommendations = productService.findForRoutine(
                category, time, skinType, goals, pageRequest);

        if (recommendations.hasContent()) {
            ProductSummaryDTO summary = recommendations.getContent().get(0);
            productRepository.findById(summary.getId())
                    .ifPresent(product -> routine.getProducts().add(product));
        }
    }

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

    private Routine findRoutineOrThrow(Long routineId) {
        return routineRepository.findById(routineId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Rutina no encontrada"));
    }

    private void assertOwner(Routine routine) {
        Long me = authUserId();
        Long ownerId = routine.getUser().getId();
        if (!me.equals(ownerId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No autorizado");
        }
    }
}