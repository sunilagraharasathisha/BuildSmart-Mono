package com.buildsmart.finance.controller;

import com.buildsmart.finance.dto.ExpenseRequestDto;
import com.buildsmart.finance.dto.ExpenseResponseDto;
import com.buildsmart.finance.service.ExpenseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/expenses")
@RequiredArgsConstructor
public class ExpenseController {

    private final ExpenseService expenseService;

    @PostMapping
    public ResponseEntity<ExpenseResponseDto> createExpense(@Valid @RequestBody ExpenseRequestDto request) {
        ExpenseResponseDto created = expenseService.createExpense(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping("/{expenseId}")
    public ResponseEntity<ExpenseResponseDto> getExpense(@PathVariable String expenseId) {
        return ResponseEntity.ok(expenseService.getExpenseById(expenseId));
    }

    @GetMapping("/project/{projectId}")
    public ResponseEntity<Page<ExpenseResponseDto>> getExpensesByProject(
            @PathVariable String projectId,
            @PageableDefault(size = 20, sort = "date") Pageable pageable) {
        return ResponseEntity.ok(expenseService.getExpensesByProject(projectId, pageable));
    }

    @GetMapping("/pending")
    public ResponseEntity<Page<ExpenseResponseDto>> getPendingExpenses(
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(expenseService.getPendingExpenses(pageable));
    }

    @PutMapping("/{expenseId}")
    public ResponseEntity<ExpenseResponseDto> updateExpense(@PathVariable String expenseId,
                                                            @Valid @RequestBody ExpenseRequestDto request) {
        return ResponseEntity.ok(expenseService.updateExpense(expenseId, request));
    }

    @DeleteMapping("/{expenseId}")
    public ResponseEntity<Void> deleteExpense(@PathVariable String expenseId) {
        expenseService.deleteExpense(expenseId);
        return ResponseEntity.noContent().build();
    }
}
