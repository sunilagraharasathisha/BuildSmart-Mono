package com.buildsmart.vendor.service.impl;

import com.buildsmart.common.exception.ResourceNotFoundException;
import com.buildsmart.common.loggers.ApplicationLogger;
import com.buildsmart.common.util.IdGeneratorUtil;
import com.buildsmart.vendor.entity.Contract;
import com.buildsmart.vendor.entity.Delivery;
import com.buildsmart.vendor.dto.DeliveryRequest;
import com.buildsmart.vendor.dto.DeliveryResponse;
import com.buildsmart.vendor.repository.ContractRepository;
import com.buildsmart.vendor.repository.DeliveryRepository;
import com.buildsmart.vendor.service.DeliveryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DeliveryServiceImpl implements DeliveryService {

    private final DeliveryRepository deliveryRepository;
    private final ContractRepository contractRepository;

    @Override
    @Transactional
    public DeliveryResponse createDelivery(DeliveryRequest request) {
        ApplicationLogger.log.info("Creating delivery");

        Contract contract = contractRepository.findById(request.contractId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Contract not found: " + request.contractId()));

        Delivery last = deliveryRepository.findTopByOrderByDeliveryIdDesc();

        Delivery delivery = new Delivery();
        delivery.setDeliveryId(
                IdGeneratorUtil.nextDeliveryId(last == null ? null : last.getDeliveryId()));
        delivery.setContract(contract);
        delivery.setDate(request.date());
        delivery.setItem(request.item());
        delivery.setQuantity(request.quantity());
        delivery.setStatus("PENDING");

        return toResponse(deliveryRepository.save(delivery));
    }

    @Override
    @Transactional(readOnly = true)
    public List<DeliveryResponse> getDeliveriesByContractId(String contractId) {
        return deliveryRepository
                .findByContractContractId(contractId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    private DeliveryResponse toResponse(Delivery delivery) {
        return DeliveryResponse.of(
                delivery.getDeliveryId(),
                delivery.getContract().getContractId(),
                delivery.getDate(),
                delivery.getItem(),
                delivery.getQuantity(),
                delivery.getStatus()
        );
    }
}