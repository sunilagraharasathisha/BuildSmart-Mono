package com.buildsmart.finance.controller;

import com.buildsmart.finance.dto.BudgetRequestDto;
import com.buildsmart.finance.dto.BudgetResponseDto;
import com.buildsmart.finance.service.BudgetService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/budgets")
@RequiredArgsConstructor
public class BudgetController {

    private final BudgetService budgetService;

    @PostMapping
    public ResponseEntity<BudgetResponseDto> createBudget(@Valid @RequestBody BudgetRequestDto request) {
        BudgetResponseDto created = budgetService.createBudget(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping("/{budgetId}")
    public ResponseEntity<BudgetResponseDto> getBudget(@PathVariable String budgetId) {
        return ResponseEntity.ok(budgetService.getBudgetById(budgetId));
    }

    @GetMapping("/project/{projectId}")
    public ResponseEntity<Page<BudgetResponseDto>> getBudgetsByProject(
            @PathVariable String projectId,
            @PageableDefault(size = 20, sort = "budgetId") Pageable pageable) {
        return ResponseEntity.ok(budgetService.getBudgetsByProject(projectId, pageable));
    }

    @PutMapping("/{budgetId}")
    public ResponseEntity<BudgetResponseDto> updateBudget(@PathVariable String budgetId,
                                                          @Valid @RequestBody BudgetRequestDto request) {
        return ResponseEntity.ok(budgetService.updateBudget(budgetId, request));
    }

    @DeleteMapping("/{budgetId}")
    public ResponseEntity<Void> deleteBudget(@PathVariable String budgetId) {
        budgetService.deleteBudget(budgetId);
        return ResponseEntity.noContent().build();
    }
}
