package com.buildsmart.projectmanager.repository;

import com.buildsmart.common.enums.Department;
import com.buildsmart.projectmanager.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TaskRepository extends JpaRepository<Task, String> {
    List<Task> findByProjectProjectId(String projectId);
    Task findTopByAssignedDepartmentOrderByTaskIdDesc(Department department);
}
