package com.buildsmart.projectmanager.service.impl;

import com.buildsmart.common.exception.ResourceNotFoundException;
import com.buildsmart.common.notification.NotificationService;
import com.buildsmart.common.util.IdGeneratorUtil;
import com.buildsmart.projectmanager.dto.TaskRequest;
import com.buildsmart.projectmanager.dto.TaskResponse;
import com.buildsmart.projectmanager.entity.Project;
import com.buildsmart.projectmanager.entity.Task;
import com.buildsmart.projectmanager.repository.ProjectRepository;
import com.buildsmart.projectmanager.repository.TaskRepository;
import com.buildsmart.projectmanager.service.TaskService;
import com.buildsmart.projectmanager.validator.TaskValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;
    private final ProjectRepository projectRepository;
    private final TaskValidator taskValidator;
    private final NotificationService notificationService;

    @Override
    @Transactional
    public TaskResponse createTask(TaskRequest request) {
        taskValidator.validate(request);
        Project project = projectRepository.findById(request.projectId())
                .orElseThrow(() -> new ResourceNotFoundException("Project not found: " + request.projectId()));
        Task lastTask = taskRepository.findTopByAssignedDepartmentOrderByTaskIdDesc(request.assignedDepartment());
        String nextTaskId = IdGeneratorUtil.nextTaskId(request.assignedDepartment(), lastTask == null ? null : lastTask.getTaskId());

        Task task = new Task();
        task.setTaskId(nextTaskId);
        task.setProject(project);
        task.setAssignedDepartment(request.assignedDepartment());
        task.setAssignedTo(request.assignedTo());
        task.setDescription(request.description());
        task.setPlannedStart(request.plannedStart());
        task.setPlannedEnd(request.plannedEnd());
        task.setActualStart(request.actualStart());
        task.setActualEnd(request.actualEnd());
        task.setStatus(request.status());
        Task savedTask = taskRepository.save(task);
        String message = String.format("You have been assigned task %s for Project %s",
                savedTask.getTaskId(), project.getProjectId());
        notificationService.createNotification(
                savedTask.getAssignedTo(),
                savedTask.getTaskId(),
                project.getProjectId(),
                message);
        return toResponse(savedTask);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TaskResponse> getTasksByProjectId(String projectId) {
        return taskRepository.findByProjectProjectId(projectId).stream().map(this::toResponse).toList();
    }

    @Override
    @Transactional
    public TaskResponse updateTask(String taskId, TaskRequest request) {
        taskValidator.validate(request);
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found: " + taskId));
        Project project = projectRepository.findById(request.projectId())
                .orElseThrow(() -> new ResourceNotFoundException("Project not found: " + request.projectId()));

        task.setProject(project);
        task.setAssignedDepartment(request.assignedDepartment());
        task.setAssignedTo(request.assignedTo());
        task.setDescription(request.description());
        task.setPlannedStart(request.plannedStart());
        task.setPlannedEnd(request.plannedEnd());
        task.setActualStart(request.actualStart());
        task.setActualEnd(request.actualEnd());
        task.setStatus(request.status());

        Task updatedTask = taskRepository.save(task);
        return toResponse(updatedTask);
    }

    @Override
    @Transactional
    public void deleteTask(String taskId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found: " + taskId));
        taskRepository.delete(task);
    }

    private TaskResponse toResponse(Task task) {
        return new TaskResponse(
                task.getTaskId(),
                task.getProject().getProjectId(),
                task.getAssignedDepartment(),
                task.getAssignedTo(),
                task.getDescription(),
                task.getPlannedStart(),
                task.getPlannedEnd(),
                task.getActualStart(),
                task.getActualEnd(),
                task.getStatus()
        );
    }
}
