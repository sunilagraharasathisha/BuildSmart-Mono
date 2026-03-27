package com.buildsmart.resource.repository;

import com.buildsmart.resource.entity.AllocationEntity;
import com.buildsmart.resource.enums.AllocationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface AllocationRepository extends JpaRepository<AllocationEntity, String> {

    Optional<AllocationEntity> findTopByOrderByAllocationIdDesc();

    List<AllocationEntity> findByProjectId(String projectId);

    List<AllocationEntity> findByResourceResourceId(String resourceId);

    List<AllocationEntity> findByProjectIdAndStatus(String projectId, AllocationStatus status);

    /**
     * Overlap check: does this resource already have a Planned/Active allocation
     * that overlaps [start .. end]?  NULL releasedDate is treated as open-ended (farFuture).
     */
    @Query("""
        select count(a) from AllocationEntity a
        where a.resource.resourceId = :resourceId
          and a.status in :statuses
          and coalesce(a.releasedDate, :farFuture) >= :start
          and coalesce(:end, :farFuture) >= a.assignedDate
    """)
    long countOverlappingForResource(@Param("resourceId") String resourceId,
                                     @Param("start") LocalDate start,
                                     @Param("end") LocalDate end,
                                     @Param("statuses") List<AllocationStatus> statuses,
                                     @Param("farFuture") LocalDate farFuture);

    /**
     * Same overlap check but excludes a specific allocation (used when updating dates).
     */
    @Query("""
        select count(a) from AllocationEntity a
        where a.resource.resourceId = :resourceId
          and a.status in :statuses
          and coalesce(a.releasedDate, :farFuture) >= :start
          and coalesce(:end, :farFuture) >= a.assignedDate
          and a.allocationId <> :excludeId
    """)
    long countOverlappingExcluding(@Param("resourceId") String resourceId,
                                   @Param("start") LocalDate start,
                                   @Param("end") LocalDate end,
                                   @Param("statuses") List<AllocationStatus> statuses,
                                   @Param("farFuture") LocalDate farFuture,
                                   @Param("excludeId") String excludeId);

    /**
     * Count Active allocations for a project on a given date (for headcount cap).
     */
    @Query("""
        select count(a) from AllocationEntity a
        where a.projectId = :projectId
          and a.status = :activeStatus
          and (
               (a.releasedDate is null and :onDate >= a.assignedDate)
            or (:onDate between a.assignedDate and a.releasedDate)
          )
    """)
    long countActiveOnDateForProject(@Param("projectId") String projectId,
                                     @Param("onDate") LocalDate onDate,
                                     @Param("activeStatus") AllocationStatus activeStatus);

    /**
     * Count Active allocations for a resource on a given date (for availability refresh).
     */
    @Query("""
        select count(a) from AllocationEntity a
        where a.resource.resourceId = :resourceId
          and a.status = :activeStatus
          and (
               (a.releasedDate is null and :onDate >= a.assignedDate)
            or (:onDate between a.assignedDate and a.releasedDate)
          )
    """)
    long countActiveOnDateForResource(@Param("resourceId") String resourceId,
                                      @Param("onDate") LocalDate onDate,
                                      @Param("activeStatus") AllocationStatus activeStatus);
}
