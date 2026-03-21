package com.buildsmart.projectmanager.repository;

import com.buildsmart.projectmanager.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TaskRepository extends JpaRepository<Task, String> {

    Optional<Task> findByTaskId(String taskId);

    boolean existsByTaskId(String taskId);

    List<Task> findByProject_ProjectId(String projectId);

    long countByTaskIdStartingWith(String prefix);
}
