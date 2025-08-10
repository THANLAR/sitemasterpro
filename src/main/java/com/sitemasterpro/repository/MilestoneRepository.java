package com.sitemasterpro.repository;

import com.sitemasterpro.entity.Milestone;
import com.sitemasterpro.entity.Project;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface MilestoneRepository extends JpaRepository<Milestone, Long> {
    List<Milestone> findByProject(Project project);
    Page<Milestone> findByProject(Project project, Pageable pageable);
    
    List<Milestone> findByIsCompleted(Boolean isCompleted);
    
    @Query("SELECT m FROM Milestone m WHERE m.plannedDate BETWEEN :startDate AND :endDate")
    List<Milestone> findByPlannedDateBetween(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
    
    @Query("SELECT m FROM Milestone m WHERE m.isCompleted = false AND m.plannedDate < :date")
    List<Milestone> findOverdueMilestones(@Param("date") LocalDate date);
    
    @Query("SELECT COUNT(m) FROM Milestone m WHERE m.project = :project AND m.isCompleted = true")
    long countCompletedMilestonesByProject(@Param("project") Project project);
    
    @Query("SELECT COUNT(m) FROM Milestone m WHERE m.project = :project")
    long countTotalMilestonesByProject(@Param("project") Project project);
}
