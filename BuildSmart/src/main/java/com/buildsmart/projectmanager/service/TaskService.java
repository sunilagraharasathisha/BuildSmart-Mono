package com.buildsmart.projectmanager.service;

import com.buildsmart.projectmanager.dto.TaskRequest;
import com.buildsmart.projectmanager.dto.TaskResponse;

import java.util.List;

public interface TaskService {
    TaskResponse createTask(TaskRequest request);
    List<TaskResponse> getTasksByProjectId(String projectId);
    TaskResponse updateTask(String taskId, TaskRequest request);
    void deleteTask(String taskId);
}
