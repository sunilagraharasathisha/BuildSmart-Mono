package com.buildsmart.finance.controller;

import com.buildsmart.finance.service.BudgetService;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


class BudgetControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Mock
    private BudgetService budgetService;

    @Test
    void shouldReturnBadRequestWhenValidationFails() throws Exception {

        doThrow(new RuntimeException("Planned budget exceeds project budget."))
                .when(budgetService)
                .validatePlannedBudget(anyString(), any(java.math.BigDecimal.class));

        mockMvc.perform(post("/api/finance/budgets")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                          "projectId": "1",
                          "category": "LABOR",
                          "plannedAmount": 120000,
                          "actualAmount": 0
                        }
                        """))
                .andExpect(status().isBadRequest());
    }
}
