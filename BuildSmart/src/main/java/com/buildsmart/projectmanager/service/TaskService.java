package com.buildsmart.projectmanager.service;

import com.buildsmart.projectmanager.dto.TaskRequestDto;
import com.buildsmart.projectmanager.dto.TaskResponseDto;
import com.buildsmart.projectmanager.entity.Task;

import java.util.List;

public interface TaskService {

    TaskResponseDto createTask(TaskRequestDto request);

    TaskResponseDto getTaskById(String taskId);

    List<TaskResponseDto> getTasksByProject(String projectId);

    TaskResponseDto updateTask(String taskId, TaskRequestDto request);

    TaskResponseDto updateTaskStatus(String taskId, Task.TaskStatus status);

    void deleteTask(String taskId);
}
