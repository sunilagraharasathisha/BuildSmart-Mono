package com.buildsmart.finance.service;

import com.buildsmart.finance.repository.BudgetRepository;
import com.buildsmart.finance.service.impl.BudgetServiceImpl;
import com.buildsmart.finance.validator.BudgetValidator;
import com.buildsmart.projectmanager.entity.Project;
import com.buildsmart.projectmanager.repository.ProjectRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Optional;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BudgetServiceTest {

    @InjectMocks
    private BudgetServiceImpl budgetService;

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private BudgetRepository budgetRepository;

    @Mock
    private BudgetValidator budgetValidator;

    @Test
    void shouldAllowPlannedBudgetExceedingProjectBudgetWithWarning() {
        String projectId = "1";
        BigDecimal plannedAmount = BigDecimal.valueOf(120000);

        Project project = new Project();
        project.setBudget(BigDecimal.valueOf(100000));

        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));
        when(budgetRepository.findByProjectProjectId(projectId)).thenReturn(Collections.emptyList());

        // Should not throw exception anymore, just allow with warning
        budgetService.validatePlannedBudget(projectId, plannedAmount);
    }
}
