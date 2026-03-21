package com.buildsmart.finance.service;

import com.buildsmart.common.util.IdGeneratorUtil;
import com.buildsmart.finance.repository.BudgetRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BudgetIdGeneratorService {

    private static final String PREFIX = "BUDBS";

    private final BudgetRepository budgetRepository;
    private final IdGeneratorUtil idGeneratorUtil;

    public BudgetIdGeneratorService(BudgetRepository budgetRepository, IdGeneratorUtil idGeneratorUtil) {
        this.budgetRepository = budgetRepository;
        this.idGeneratorUtil = idGeneratorUtil;
    }

    @Transactional(readOnly = true)
    public String generateNextBudgetId() {
        long count = budgetRepository.countByBudgetIdStartingWith(PREFIX);
        return idGeneratorUtil.generateId(PREFIX, count + 1);
    }
}
