package com.buildsmart.projectmanager.controller;

import com.buildsmart.common.enums.MilestoneStatus;
import com.buildsmart.projectmanager.dto.MilestoneResponse;
import com.buildsmart.projectmanager.service.MilestoneService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class MilestoneControllerTest {

    private MockMvc mockMvc;

    @Mock
    private MilestoneService milestoneService;

    @InjectMocks
    private MilestoneController milestoneController;

    private MilestoneResponse milestoneResponse;
    private MilestoneRequest milestoneRequest;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(milestoneController).build();
        objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();

        milestoneRequest = new MilestoneRequest(
                "PRJ001",
                "Test Milestone",
                "Test milestone description",
                LocalDate.of(2026, 6, 1),
                null,
                MilestoneStatus.PLANNED
        );

        milestoneResponse = new MilestoneResponse(
                "MILBS001",
                "PRJ001",
                "Test Milestone",
                "Test milestone description",
                LocalDate.of(2026, 6, 1),
                null,
                MilestoneStatus.PLANNED
        );
    }

    @Test
    void testCreateMilestone() throws Exception {
        when(milestoneService.createMilestone(any(MilestoneRequest.class))).thenReturn(milestoneResponse);

        mockMvc.perform(post("/api/project-manager/milestones")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(milestoneRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.milestoneId").value("MILBS001"))
                .andExpect(jsonPath("$.milestoneName").value("Test Milestone"));
    }

    @Test
    void testGetMilestoneById() throws Exception {
        when(milestoneService.getMilestoneById("MILBS001")).thenReturn(milestoneResponse);

        mockMvc.perform(get("/api/project-manager/milestones/MILBS001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.milestoneId").value("MILBS001"))
                .andExpect(jsonPath("$.milestoneName").value("Test Milestone"));
    }

    @Test
    void testGetMilestonesByProjectId() throws Exception {
        when(milestoneService.getMilestonesByProjectId("PRJ001")).thenReturn(List.of(milestoneResponse));

        mockMvc.perform(get("/api/project-manager/milestones/project/PRJ001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].milestoneId").value("MILBS001"));
    }

    @Test
    void testUpdateMilestone() throws Exception {
        when(milestoneService.updateMilestone(any(), any(MilestoneRequest.class))).thenReturn(milestoneResponse);

        mockMvc.perform(put("/api/project-manager/milestones/MILBS001")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(milestoneRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.milestoneId").value("MILBS001"));
    }

    @Test
    void testDeleteMilestone() throws Exception {
        doNothing().when(milestoneService).deleteMilestone("MILBS001");

        mockMvc.perform(delete("/api/project-manager/milestones/MILBS001"))
                .andExpect(status().isNoContent());
    }
}