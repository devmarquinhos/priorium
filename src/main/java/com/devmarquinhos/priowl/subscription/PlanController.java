package com.devmarquinhos.priowl.subscription;

import com.devmarquinhos.priowl.subscription.dto.PlanRequest;
import com.devmarquinhos.priowl.subscription.dto.PlanResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/plans")
public class PlanController {

    private final PlanService planService;

    public PlanController(PlanService planService) {
        this.planService = planService;
    }

    @GetMapping
    public ResponseEntity<List<PlanResponse>> getAllPlans() {
        return ResponseEntity.ok(planService.getAllPlans());
    }

    @PostMapping
    public ResponseEntity<?> createPlan(@RequestBody PlanRequest request) {
        try {
            return ResponseEntity.status(201).body(planService.createPlan(request));
        } catch (RuntimeException e) {
            return ResponseEntity.status(403).body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updatePlan(@PathVariable Long id, @RequestBody PlanRequest request) {
        try {
            return ResponseEntity.ok(planService.updatePlan(id, request));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}