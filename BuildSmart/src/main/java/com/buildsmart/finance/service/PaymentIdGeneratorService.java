package com.buildsmart.finance.service;

import com.buildsmart.common.util.IdGeneratorUtil;
import com.buildsmart.finance.repository.PaymentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PaymentIdGeneratorService {

    private static final String PREFIX = "PAYBS";

    private final PaymentRepository paymentRepository;
    private final IdGeneratorUtil idGeneratorUtil;

    public PaymentIdGeneratorService(PaymentRepository paymentRepository, IdGeneratorUtil idGeneratorUtil) {
        this.paymentRepository = paymentRepository;
        this.idGeneratorUtil = idGeneratorUtil;
    }

    @Transactional(readOnly = true)
    public String generateNextPaymentId() {
        long count = paymentRepository.countByPaymentIdStartingWith(PREFIX);
        return idGeneratorUtil.generateId(PREFIX, count + 1);
    }
}
