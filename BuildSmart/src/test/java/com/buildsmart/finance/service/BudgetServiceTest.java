package com.buildsmart.finance.service;

import com.buildsmart.finance.service.impl.BudgetServiceImpl;
import com.buildsmart.projectmanager.entity.Project;
import com.buildsmart.projectmanager.repository.ProjectRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BudgetServiceTest {

    @InjectMocks
    private BudgetServiceImpl budgetService;

    @Mock
    private ProjectRepository projectRepository;

    @Test
    void shouldThrowExceptionWhenPlannedExceedsProjectBudget() {
        String projectId = "1";
        BigDecimal plannedAmount = BigDecimal.valueOf(120000);

        Project project = new Project();
        project.setBudget(BigDecimal.valueOf(100000));

        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> budgetService.validatePlannedBudget(projectId, plannedAmount));

        assertEquals("Planned budget exceeds project budget.", exception.getMessage());
    }
}
