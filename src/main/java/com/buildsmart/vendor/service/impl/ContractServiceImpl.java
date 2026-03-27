package com.buildsmart.vendor.service.impl;

import com.buildsmart.common.exception.ResourceNotFoundException;
import com.buildsmart.common.loggers.ApplicationLogger;
import com.buildsmart.common.util.IdGeneratorUtil;
import com.buildsmart.vendor.entity.Contract;
import com.buildsmart.vendor.entity.Vendor;
import com.buildsmart.projectmanager.entity.Project;
import com.buildsmart.projectmanager.repository.ProjectRepository;
import com.buildsmart.vendor.dto.ContractRequest;
import com.buildsmart.vendor.dto.ContractResponse;
import com.buildsmart.vendor.repository.ContractRepository;
import com.buildsmart.vendor.repository.VendorRepository;
import com.buildsmart.vendor.service.ContractService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ContractServiceImpl implements ContractService {

    private final ContractRepository contractRepository;
    private final VendorRepository vendorRepository;
    private final ProjectRepository projectRepository;

    @Override
    @Transactional
    public ContractResponse createContract(ContractRequest request) {
        ApplicationLogger.log.info("Creating contract");

        Vendor vendor = vendorRepository.findById(request.vendorId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Vendor not found: " + request.vendorId()));

        Project project = projectRepository.findById(request.projectId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Project not found: " + request.projectId()));

        Contract last = contractRepository.findTopByOrderByContractIdDesc();

        Contract contract = new Contract();
        contract.setContractId(
                IdGeneratorUtil.nextContractId(last == null ? null : last.getContractId()));
        contract.setVendor(vendor);
        contract.setProject(project);
        contract.setStartDate(request.startDate());
        contract.setEndDate(request.endDate());
        contract.setValue(request.value());
        contract.setStatus("ACTIVE");

        return toResponse(contractRepository.save(contract));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ContractResponse> getContractsByProjectProjectId(String projectId) {
        return contractRepository
                .findByProjectProjectId(projectId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    private ContractResponse toResponse(Contract contract) {
        return ContractResponse.of(
                contract.getContractId(),
                contract.getVendor().getVendorId(),
                contract.getProject().getProjectId(),
                contract.getStartDate(),
                contract.getEndDate(),
                contract.getValue(),
                contract.getStatus()
        );
    }
}