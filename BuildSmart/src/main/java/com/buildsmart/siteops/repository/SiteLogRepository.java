package com.buildsmart.siteops.repository;

import com.buildsmart.siteops.entity.SiteLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface SiteLogRepository extends JpaRepository<SiteLog, String> {

    Optional<SiteLog> findTopByOrderByLogIdDesc();

    boolean existsByProjectIdAndLogDate(String projectId, LocalDate logDate);

    List<SiteLog> findByProjectIdOrderByLogDateDesc(String projectId);

    Optional<SiteLog> findByProjectIdAndLogDate(String projectId, LocalDate logDate);

    List<SiteLog> findByProjectIdAndLogDateBetweenOrderByLogDateDesc(
            String projectId, LocalDate from, LocalDate to);

    Optional<SiteLog> findTopByProjectIdOrderByLogDateDesc(String projectId);
}
