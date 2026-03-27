package com.buildsmart.vendor.service;

import com.buildsmart.vendor.dto.ContractRequest;
import com.buildsmart.vendor.dto.ContractResponse;

import java.util.List;

public interface ContractService {

    ContractResponse createContract(ContractRequest request);

    List<ContractResponse> getContractsByProjectProjectId(String projectId);
}