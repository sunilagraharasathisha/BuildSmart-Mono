package com.buildsmart.siteops.service;

import com.buildsmart.siteops.dto.IssueRequest;
import com.buildsmart.siteops.dto.IssueResponse;
import com.buildsmart.siteops.dto.IssueUpdateRequest;
import com.buildsmart.siteops.enums.IssueSeverity;
import com.buildsmart.siteops.enums.IssueStatus;

import java.util.List;

public interface IssueService {

    IssueResponse createIssue(IssueRequest request);

    IssueResponse getIssueById(String issueId);

    List<IssueResponse> getIssuesByProject(String projectId);

    List<IssueResponse> getIssuesByProjectAndStatus(String projectId, IssueStatus status);

    List<IssueResponse> getIssuesByProjectAndSeverity(String projectId, IssueSeverity severity);

    List<IssueResponse> getIssuesByProjectAndReporter(String projectId, String reportedBy);

    List<IssueResponse> getIssuesByLogId(String logId);

    IssueResponse updateIssue(String issueId, IssueUpdateRequest request);
}
