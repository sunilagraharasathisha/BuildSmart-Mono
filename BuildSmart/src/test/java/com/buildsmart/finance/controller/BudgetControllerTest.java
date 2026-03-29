package com.buildsmart.finance.controller;

import com.buildsmart.finance.dto.BudgetResponse;
import com.buildsmart.finance.service.BudgetService;
import com.buildsmart.common.enums.BudgetCategory;
import com.buildsmart.common.enums.BudgetStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.OngoingStubbing;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import org.mockito.Mock;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import java.math.BigDecimal;

@ExtendWith(MockitoExtension.class)
class BudgetControllerTest {

        @Autowired
        private MockMvc mockMvc;

        @Mock
        private BudgetService budgetService;

        @Test
        void shouldCreateBudgetSuccessfullyEvenWhenExceedingProjectBudget() throws Exception {
                BudgetResponse mockResponse = new BudgetResponse(
                                "BUD001", "PROJ001", BudgetCategory.LABOR,
                                BigDecimal.valueOf(120000), BigDecimal.ZERO, BigDecimal.valueOf(-120000),
                                BudgetStatus.OVER_BUDGET);

                when(budgetService.createBudget(any())).thenReturn(mockResponse);

                mockMvc.perform(post("/api/finance/budgets")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                                {
                                                  "projectId": "PROJ001",
                                                  "category": "LABOR",
                                                  "plannedAmount": 120000,
                                                  "actualAmount": 0
                                                }
                                                """))
                                .andExpect(status().isCreated())
                                .andExpect(jsonPath("$.budgetId").value("BUD001"));
        }

        private OngoingStubbing<BudgetResponse> when(Object validatePlannedBudget) {
                // TODO Auto-generated method stub
                throw new UnsupportedOperationException("Unimplemented method 'when'");
        }
}
