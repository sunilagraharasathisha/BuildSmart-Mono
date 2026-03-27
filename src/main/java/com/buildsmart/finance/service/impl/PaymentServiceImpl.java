package com.buildsmart.finance.service.impl;

import com.buildsmart.common.enums.PaymentStatus;
import com.buildsmart.common.exception.DuplicateResourceException;
import com.buildsmart.common.util.IdGeneratorUtil;
import com.buildsmart.finance.dto.PaymentRequest;
import com.buildsmart.finance.dto.PaymentResponse;
import com.buildsmart.finance.entity.Payment;
import com.buildsmart.finance.repository.PaymentRepository;
import com.buildsmart.finance.service.PaymentService;
import com.buildsmart.finance.validator.PaymentValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final PaymentValidator paymentValidator;

    @Override
    @Transactional
    public PaymentResponse createPayment(PaymentRequest request) {
        paymentValidator.validate(request);

        if (request.status() == PaymentStatus.SUCCESS &&
                paymentRepository.existsByInvoiceIdAndStatus(
                        request.invoiceId(), PaymentStatus.SUCCESS)) {

            throw new DuplicateResourceException(
                    "Payment already completed for invoice: " + request.invoiceId()
            );
        }

        Payment last = paymentRepository.findTopByOrderByPaymentIdDesc();
        Payment payment = new Payment();
        payment.setPaymentId(IdGeneratorUtil.nextPaymentId(last == null ? null : last.getPaymentId()));
        payment.setInvoiceId(request.invoiceId());
        payment.setAmount(request.amount());
        payment.setDate(request.date());
        payment.setStatus(request.status());
        Payment saved = paymentRepository.save(payment);
        return new PaymentResponse(saved.getPaymentId(), saved.getInvoiceId(), saved.getAmount(), saved.getDate(), saved.getStatus());
    }
}
