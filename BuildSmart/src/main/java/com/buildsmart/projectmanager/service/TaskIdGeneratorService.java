package com.buildsmart.projectmanager.service;

import com.buildsmart.common.util.IdGeneratorUtil;
import com.buildsmart.projectmanager.entity.Task;
import com.buildsmart.projectmanager.repository.TaskRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

/**
 * Generates Task IDs based on department:
 * Finance -> FINBS001, Vendor -> VENBS001, Safety -> SAFBS001, Site -> SITBS001
 */
@Service
public class TaskIdGeneratorService {

    private static final Map<Task.AssignedDepartment, String> PREFIX_MAP = Map.of(
            Task.AssignedDepartment.FINANCE, "FINBS",
            Task.AssignedDepartment.VENDOR, "VENBS",
            Task.AssignedDepartment.SAFETY, "SAFBS",
            Task.AssignedDepartment.SITE, "SITBS"
    );

    private final TaskRepository taskRepository;
    private final IdGeneratorUtil idGeneratorUtil;

    public TaskIdGeneratorService(TaskRepository taskRepository, IdGeneratorUtil idGeneratorUtil) {
        this.taskRepository = taskRepository;
        this.idGeneratorUtil = idGeneratorUtil;
    }

    @Transactional(readOnly = true)
    public String generateNextTaskId(Task.AssignedDepartment department) {
        String prefix = PREFIX_MAP.get(department);
        if (prefix == null) {
            throw new IllegalArgumentException("Unknown department: " + department);
        }
        long count = taskRepository.countByTaskIdStartingWith(prefix);
        return idGeneratorUtil.generateId(prefix, count + 1);
    }
}
