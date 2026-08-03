package com.devmarquinhos.priowl.service;

import com.devmarquinhos.priowl.dto.PlanRequest;
import com.devmarquinhos.priowl.dto.PlanResponse;
import com.devmarquinhos.priowl.model.Plan;
import com.devmarquinhos.priowl.model.User;
import com.devmarquinhos.priowl.repository.PlanRepository;
import com.devmarquinhos.priowl.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PlanService {
    private final PlanRepository planRepository;
    private final UserRepository userRepository;

    public PlanService(PlanRepository planRepository, UserRepository userRepository) {
        this.planRepository = planRepository;
        this.userRepository = userRepository;
    }

    private void verifyAdmin() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado."));

        if (!user.getIsAdmin()) {
            throw new RuntimeException("Acesso negado. Apenas administradores podem realizar esta ação.");
        }
    }

    public List<PlanResponse> getAllPlans() {
        return planRepository.findAll().stream()
                .map(plan -> new PlanResponse(
                        plan.getId(),
                        plan.getName(),
                        plan.getDescription(),
                        plan.getPrice(),
                        plan.getMaxTasks(),
                        plan.getIsActive()
                ))
                .collect(Collectors.toList());
    }

    @Transactional
    public PlanResponse createPlan(PlanRequest request) {
        verifyAdmin();

        Plan plan = new Plan();
        plan.setName(request.name());
        plan.setDescription(request.description());
        plan.setPrice(request.price());
        plan.setMaxTasks(request.maxTasks());
        plan.setIsActive(true);

        Plan savedPlan = planRepository.save(plan);

        return new PlanResponse(savedPlan.getId(), savedPlan.getName(), savedPlan.getDescription(), savedPlan.getPrice(), savedPlan.getMaxTasks(), savedPlan.getIsActive());
    }

    @Transactional
    public PlanResponse updatePlan(Long id, PlanRequest request) {
        verifyAdmin();

        Plan plan = planRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Plano não encontrado."));

        plan.setName(request.name());
        plan.setDescription(request.description());
        plan.setPrice(request.price());
        plan.setMaxTasks(request.maxTasks());

        Plan updatedPlan = planRepository.save(plan);

        return new PlanResponse(updatedPlan.getId(), updatedPlan.getName(), updatedPlan.getDescription(), updatedPlan.getPrice(), updatedPlan.getMaxTasks(), updatedPlan.getIsActive());
    }
}
