package com.buildsmart.finance.controller;

import com.buildsmart.finance.dto.BudgetRequest;
import com.buildsmart.finance.dto.BudgetResponse;
import com.buildsmart.finance.service.BudgetService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/finance/budgets")
@RequiredArgsConstructor
@Tag(name = "Finance APIs", description = "Budget management endpoints")
@PreAuthorize("hasAnyRole('ADMIN','FINANCE_OFFICER')")
public class BudgetController {

    private final BudgetService budgetService;

    @PostMapping
    @Operation(summary = "Create budget")
    @ApiResponse(responseCode = "201", description = "Budget created")
    public ResponseEntity<BudgetResponse> createBudget(@Valid @RequestBody BudgetRequest request) {
        budgetService.validatePlannedBudget(request.projectId(), request.plannedAmount());
        return ResponseEntity.status(HttpStatus.CREATED).body(budgetService.createBudget(request));
    }

    @GetMapping("/project/{projectId}")
    @Operation(summary = "Get budgets by project ID")
    @ApiResponse(responseCode = "200", description = "Budgets fetched")
    public ResponseEntity<List<BudgetResponse>> getBudgetsByProjectId(@PathVariable String projectId) {
        return ResponseEntity.ok(budgetService.getBudgetsByProjectId(projectId));
    }

    @PutMapping("/{budgetId}")
    @Operation(summary = "Update budget")
    @ApiResponse(responseCode = "200", description = "Budget updated")
    public ResponseEntity<BudgetResponse> updateBudget(
            @PathVariable String budgetId,
            @Valid @RequestBody BudgetRequest request) {
        return ResponseEntity.ok(budgetService.updateBudget(budgetId, request));
    }

    @DeleteMapping("/{budgetId}")
    @Operation(summary = "Delete budget")
    @ApiResponse(responseCode = "204", description = "Budget deleted")
    public ResponseEntity<Void> deleteBudget(@PathVariable String budgetId) {
        budgetService.deleteBudget(budgetId);
        return ResponseEntity.noContent().build();
    }
}
