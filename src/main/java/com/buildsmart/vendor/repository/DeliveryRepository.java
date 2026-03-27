package com.buildsmart.vendor.repository;

import com.buildsmart.vendor.entity.Delivery;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DeliveryRepository extends JpaRepository<Delivery, String> {

    Delivery findTopByOrderByDeliveryIdDesc();

    List<Delivery> findByContractContractId(String contractId);

    List<Delivery> findByStatus(String status);
}