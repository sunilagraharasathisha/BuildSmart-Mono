package com.buildsmart.siteops.repository;

import com.buildsmart.siteops.entity.Issue;
import com.buildsmart.siteops.enums.IssueSeverity;
import com.buildsmart.siteops.enums.IssueStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface IssueRepository extends JpaRepository<Issue, String> {

    Optional<Issue> findTopByOrderByIssueIdDesc();

    List<Issue> findByProjectIdOrderByReportedAtDesc(String projectId);

    List<Issue> findByProjectIdAndStatus(String projectId, IssueStatus status);

    List<Issue> findByProjectIdAndSeverity(String projectId, IssueSeverity severity);

    List<Issue> findByProjectIdAndReportedBy(String projectId, String reportedBy);

    List<Issue> findByLogId(String logId);

    long countByProjectIdAndStatus(String projectId, IssueStatus status);
}
