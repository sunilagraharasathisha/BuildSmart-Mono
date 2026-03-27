package com.buildsmart.projectmanager.repository;

import com.buildsmart.projectmanager.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProjectRepository extends JpaRepository<Project, String> {
    boolean existsByProjectNameIgnoreCase(String projectName);
    Optional<Project> findTopByOrderByProjectIdDesc();
}
