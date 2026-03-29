package com.buildsmart.finance.controller;

import com.buildsmart.finance.dto.ExpenseRequest;
import com.buildsmart.finance.dto.ExpenseResponse;
import com.buildsmart.finance.service.ExpenseService;
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
@RequestMapping("/api/finance/expenses")
@RequiredArgsConstructor
@Tag(name = "Finance APIs", description = "Expense management endpoints")
@PreAuthorize("hasAnyRole('ADMIN','FINANCE_OFFICER')")
public class ExpenseController {

    private final ExpenseService expenseService;

    @PostMapping
    @Operation(summary = "Create expense")
    @ApiResponse(responseCode = "201", description = "Expense created")
    public ResponseEntity<ExpenseResponse> createExpense(@Valid @RequestBody ExpenseRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(expenseService.createExpense(request));
    }

    @GetMapping("/project/{projectId}")
    @Operation(summary = "Get expenses by project ID")
    @ApiResponse(responseCode = "200", description = "Expenses fetched")
    public ResponseEntity<List<ExpenseResponse>> getExpensesByProjectId(@PathVariable String projectId) {
        return ResponseEntity.ok(expenseService.getExpensesByProjectId(projectId));
    }

    @PutMapping("/{expenseId}")
    @Operation(summary = "Update expense")
    @ApiResponse(responseCode = "200", description = "Expense updated")
    public ResponseEntity<ExpenseResponse> updateExpense(
            @PathVariable String expenseId,
            @Valid @RequestBody ExpenseRequest request) {
        return ResponseEntity.ok(expenseService.updateExpense(expenseId, request));
    }

    @DeleteMapping("/{expenseId}")
    @Operation(summary = "Delete expense")
    @ApiResponse(responseCode = "204", description = "Expense deleted")
    public ResponseEntity<Void> deleteExpense(@PathVariable String expenseId) {
        expenseService.deleteExpense(expenseId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{expenseId}/approve")
    @Operation(summary = "Approve expense")
    @ApiResponse(responseCode = "200", description = "Expense approved")
    public ResponseEntity<ExpenseResponse> approveExpense(@PathVariable String expenseId) {
        return ResponseEntity.ok(expenseService.approveExpense(expenseId));
    }

    @PostMapping("/{expenseId}/reject")
    @Operation(summary = "Reject expense")
    @ApiResponse(responseCode = "200", description = "Expense rejected")
    public ResponseEntity<ExpenseResponse> rejectExpense(@PathVariable String expenseId) {
        return ResponseEntity.ok(expenseService.rejectExpense(expenseId));
    }
}
