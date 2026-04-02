package com.buildsmart.projectmanager.validator;

import com.buildsmart.common.enums.MilestoneStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class MilestoneValidatorTest {

    @InjectMocks
    private MilestoneValidator milestoneValidator;

    private MilestoneRequest validRequest;

    @BeforeEach
    void setUp() {
        validRequest = new MilestoneRequest(
                "PRJ001",
                "Test Milestone",
                "Test milestone description",
                LocalDate.of(2026, 6, 1),
                null,
                MilestoneStatus.PLANNED
        );
    }

    @Test
    void testValidateValidMilestone() {
        assertDoesNotThrow(() -> milestoneValidator.validate(validRequest));
    }

    @Test
    void testValidatePastDueDate() {
        MilestoneRequest invalidRequest = new MilestoneRequest(
                "PRJ001",
                "Test Milestone",
                "Test milestone description",
                LocalDate.of(2024, 1, 1), // Past date
                null,
                MilestoneStatus.PLANNED
        );

        assertThrows(IllegalArgumentException.class,
                () -> milestoneValidator.validate(invalidRequest));
    }

    @Test
    void testValidateFutureAchievedDate() {
        MilestoneRequest invalidRequest = new MilestoneRequest(
                "PRJ001",
                "Test Milestone",
                "Test milestone description",
                LocalDate.of(2026, 6, 1),
                LocalDate.of(2026, 12, 31), // Future date
                MilestoneStatus.ACHIEVED
        );

        assertThrows(IllegalArgumentException.class,
                () -> milestoneValidator.validate(invalidRequest));
    }

    @Test
    void testValidateAchievedDateAfterDueDate() {
        MilestoneRequest invalidRequest = new MilestoneRequest(
                "PRJ001",
                "Test Milestone",
                "Test milestone description",
                LocalDate.of(2026, 6, 1),
                LocalDate.of(2026, 6, 15), // After due date
                MilestoneStatus.ACHIEVED
        );

        assertThrows(IllegalArgumentException.class,
                () -> milestoneValidator.validate(invalidRequest));
    }

    @Test
    void testValidateValidAchievedMilestone() {
        MilestoneRequest validAchievedRequest = new MilestoneRequest(
                "PRJ001",
                "Test Milestone",
                "Test milestone description",
                LocalDate.of(2026, 6, 1),
                LocalDate.of(2026, 4, 1), // Past date (before due date)
                MilestoneStatus.ACHIEVED
        );

        assertDoesNotThrow(() -> milestoneValidator.validate(validAchievedRequest));
    }
}