package com.buildsmart.projectmanager.service;

import com.buildsmart.projectmanager.dto.MilestoneRequest;
import com.buildsmart.projectmanager.dto.MilestoneResponse;
import jakarta.validation.Valid;

import java.util.List;

public interface MilestoneService {
    MilestoneResponse createMilestone(@Valid MilestoneRequest request);
    MilestoneResponse getMilestoneById(String milestoneId);
    List<MilestoneResponse> getMilestonesByProjectId(String projectId);
    MilestoneResponse updateMilestone(String milestoneId, MilestoneRequest request);
    void deleteMilestone(String milestoneId);
}