package com.buildsmart.finance.service;

import com.buildsmart.common.util.IdGeneratorUtil;
import com.buildsmart.finance.repository.ExpenseRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ExpenseIdGeneratorService {

    private static final String PREFIX = "EXPBS";

    private final ExpenseRepository expenseRepository;
    private final IdGeneratorUtil idGeneratorUtil;

    public ExpenseIdGeneratorService(ExpenseRepository expenseRepository, IdGeneratorUtil idGeneratorUtil) {
        this.expenseRepository = expenseRepository;
        this.idGeneratorUtil = idGeneratorUtil;
    }

    @Transactional(readOnly = true)
    public String generateNextExpenseId() {
        long count = expenseRepository.countByExpenseIdStartingWith(PREFIX);
        return idGeneratorUtil.generateId(PREFIX, count + 1);
    }
}
