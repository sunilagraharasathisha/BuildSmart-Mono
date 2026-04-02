package com.buildsmart.projectmanager.service;

import com.buildsmart.common.enums.MilestoneStatus;
import com.buildsmart.common.exception.DuplicateResourceException;
import com.buildsmart.common.exception.ResourceNotFoundException;
import com.buildsmart.projectmanager.dto.MilestoneResponse;
import com.buildsmart.projectmanager.entity.Milestone;
import com.buildsmart.projectmanager.entity.Project;
import com.buildsmart.projectmanager.repository.MilestoneRepository;
import com.buildsmart.projectmanager.repository.ProjectRepository;
import com.buildsmart.projectmanager.validator.MilestoneValidator;
import com.buildsmart.projectmanager.service.impl.MilestoneServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MilestoneServiceTest {

    @Mock
    private MilestoneRepository milestoneRepository;

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private MilestoneValidator milestoneValidator;

    @InjectMocks
    private MilestoneServiceImpl milestoneService;

    private Project testProject;
    private Milestone testMilestone;
    private MilestoneRequest milestoneRequest;

    @BeforeEach
    void setUp() {
        testProject = new Project();
        testProject.setProjectId("PRJ001");
        testProject.setProjectName("Test Project");

        testMilestone = new Milestone();
        testMilestone.setMilestoneId("MILBS001");
        testMilestone.setProject(testProject);
        testMilestone.setMilestoneName("Test Milestone");
        testMilestone.setDescription("Test milestone description");
        testMilestone.setDueDate(LocalDate.of(2026, 6, 1));
        testMilestone.setAchievedDate(null);
        testMilestone.setStatus(MilestoneStatus.PLANNED);

        milestoneRequest = new MilestoneRequest(
                "PRJ001",
                "Test Milestone",
                "Test milestone description",
                LocalDate.of(2026, 6, 1),
                null,
                MilestoneStatus.PLANNED
        );
    }

    @Test
    void testCreateMilestone() {
        when(projectRepository.findById("PRJ001")).thenReturn(Optional.of(testProject));
        when(milestoneRepository.existsByMilestoneNameIgnoreCaseAndProject_ProjectId(any(), any())).thenReturn(false);
        when(milestoneRepository.save(any(Milestone.class))).thenReturn(testMilestone);

        MilestoneResponse response = milestoneService.createMilestone(milestoneRequest);

        assertNotNull(response);
        assertEquals("MILBS001", response.milestoneId());
        assertEquals("PRJ001", response.projectId());
        assertEquals("Test Milestone", response.milestoneName());
        verify(milestoneValidator).validate(milestoneRequest);
        verify(milestoneRepository).save(any(Milestone.class));
    }

    @Test
    void testCreateMilestone_ProjectNotFound() {
        when(projectRepository.findById("PRJ001")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> milestoneService.createMilestone(milestoneRequest));
    }

    @Test
    void testCreateMilestone_DuplicateName() {
        when(projectRepository.findById("PRJ001")).thenReturn(Optional.of(testProject));
        when(milestoneRepository.existsByMilestoneNameIgnoreCaseAndProject_ProjectId(any(), any())).thenReturn(true);

        assertThrows(DuplicateResourceException.class,
                () -> milestoneService.createMilestone(milestoneRequest));
    }

    @Test
    void testGetMilestoneById() {
        when(milestoneRepository.findById("MILBS001")).thenReturn(Optional.of(testMilestone));

        MilestoneResponse response = milestoneService.getMilestoneById("MILBS001");

        assertNotNull(response);
        assertEquals("MILBS001", response.milestoneId());
        assertEquals("Test Milestone", response.milestoneName());
    }

    @Test
    void testGetMilestoneById_NotFound() {
        when(milestoneRepository.findById("MILBS001")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> milestoneService.getMilestoneById("MILBS001"));
    }

    @Test
    void testGetMilestonesByProjectId() {
        when(projectRepository.existsById("PRJ001")).thenReturn(true);
        when(milestoneRepository.findByProject_ProjectId("PRJ001")).thenReturn(List.of(testMilestone));

        List<MilestoneResponse> responses = milestoneService.getMilestonesByProjectId("PRJ001");

        assertNotNull(responses);
        assertEquals(1, responses.size());
        assertEquals("MILBS001", responses.get(0).milestoneId());
    }

    @Test
    void testGetMilestonesByProjectId_ProjectNotFound() {
        when(projectRepository.existsById("PRJ001")).thenReturn(false);

        assertThrows(ResourceNotFoundException.class,
                () -> milestoneService.getMilestonesByProjectId("PRJ001"));
    }

    @Test
    void testUpdateMilestone() {
        when(milestoneRepository.findById("MILBS001")).thenReturn(Optional.of(testMilestone));
        when(projectRepository.findById("PRJ001")).thenReturn(Optional.of(testProject));
        when(milestoneRepository.save(any(Milestone.class))).thenReturn(testMilestone);

        MilestoneResponse response = milestoneService.updateMilestone("MILBS001", milestoneRequest);

        assertNotNull(response);
        assertEquals("MILBS001", response.milestoneId());
        verify(milestoneValidator).validate(milestoneRequest);
        verify(milestoneRepository).save(any(Milestone.class));
    }

    @Test
    void testUpdateMilestone_NotFound() {
        when(milestoneRepository.findById("MILBS001")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> milestoneService.updateMilestone("MILBS001", milestoneRequest));
    }

    @Test
    void testDeleteMilestone() {
        when(milestoneRepository.findById("MILBS001")).thenReturn(Optional.of(testMilestone));

        assertDoesNotThrow(() -> milestoneService.deleteMilestone("MILBS001"));
        verify(milestoneRepository).delete(testMilestone);
    }

    @Test
    void testDeleteMilestone_NotFound() {
        when(milestoneRepository.findById("MILBS001")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> milestoneService.deleteMilestone("MILBS001"));
    }
}