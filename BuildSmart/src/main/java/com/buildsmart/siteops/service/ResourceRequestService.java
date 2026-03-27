package com.buildsmart.siteops.service;

import com.buildsmart.siteops.dto.ResourceRequestApprovalRequest;
import com.buildsmart.siteops.dto.ResourceRequestCreateRequest;
import com.buildsmart.siteops.dto.ResourceRequestRejectionRequest;
import com.buildsmart.siteops.dto.ResourceRequestResponse;
import com.buildsmart.siteops.enums.ResourceRequestStatus;

import java.util.List;

public interface ResourceRequestService {

    /** Site Engineer raises a new request. */
    ResourceRequestResponse createRequest(ResourceRequestCreateRequest request);

    /** Get a single request by ID. */
    ResourceRequestResponse getRequestById(String requestId);

    /** All requests for a project (PM's dashboard view). */
    List<ResourceRequestResponse> getRequestsByProject(String projectId);

    /** Pending requests for a project — what the PM needs to action. */
    List<ResourceRequestResponse> getPendingRequestsByProject(String projectId);

    /** All requests raised by a specific Site Engineer. */
    List<ResourceRequestResponse> getRequestsByEngineer(String requestedBy);

    /** Requests linked to a specific site log. */
    List<ResourceRequestResponse> getRequestsByLog(String logId);

    /**
     * PM approves the request and assigns a specific resource.
     * Auto-creates an Active Allocation in the Resource module.
     */
    ResourceRequestResponse approveRequest(String requestId, ResourceRequestApprovalRequest request);

    /** PM rejects the request with a mandatory reason. */
    ResourceRequestResponse rejectRequest(String requestId, ResourceRequestRejectionRequest request);

    /**
     * PM marks the resource as released (work is done).
     * Transitions underlying Allocation to Released.
     */
    ResourceRequestResponse releaseRequest(String requestId, String actionedBy);

    /** Site Engineer cancels a PENDING request before PM acts. */
    ResourceRequestResponse cancelRequest(String requestId, String cancelledBy);
}
