package com.sitemasterpro.repository;

import com.sitemasterpro.entity.ProjectMilestone;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ProjectMilestoneRepository extends JpaRepository<ProjectMilestone, Long> {
    List<ProjectMilestone> findByProjectId(Long projectId);
    
    List<ProjectMilestone> findByStatus(ProjectMilestone.MilestoneStatus status);
    
    @Query("SELECT pm FROM ProjectMilestone pm WHERE pm.plannedEndDate < :date AND pm.status != 'COMPLETED'")
    List<ProjectMilestone> findOverdueMilestones(@Param("date") LocalDate date);
    
    @Query("SELECT pm FROM ProjectMilestone pm WHERE pm.project.id = :projectId AND pm.status = :status")
    List<ProjectMilestone> findByProjectIdAndStatus(@Param("projectId") Long projectId, 
                                                   @Param("status") ProjectMilestone.MilestoneStatus status);
    
    @Query("SELECT pm FROM ProjectMilestone pm WHERE pm.plannedStartDate BETWEEN :startDate AND :endDate")
    List<ProjectMilestone> findMilestonesByDateRange(@Param("startDate") LocalDate startDate, 
                                                    @Param("endDate") LocalDate endDate);
}
