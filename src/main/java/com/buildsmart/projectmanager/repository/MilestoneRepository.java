package com.buildsmart.projectmanager.repository;

import com.buildsmart.projectmanager.entity.Milestone;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MilestoneRepository extends JpaRepository<Milestone, String> {
    List<Milestone> findByProject_ProjectId(String projectId);
    boolean existsByMilestoneNameIgnoreCaseAndProject_ProjectId(String milestoneName, String projectId);
    Optional<Milestone> findTopByOrderByMilestoneIdDesc();
}