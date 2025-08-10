package com.sitemasterpro.repository;

import com.sitemasterpro.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {
    List<Project> findByStatus(Project.ProjectStatus status);
    
    @Query("SELECT p FROM Project p WHERE p.endDate < :date AND p.status != 'COMPLETED'")
    List<Project> findOverdueProjects(@Param("date") LocalDate date);
    
    @Query("SELECT p FROM Project p WHERE p.completionPercentage < :threshold")
    List<Project> findProjectsWithLowProgress(@Param("threshold") BigDecimal threshold);
    
    @Query("SELECT p FROM Project p WHERE p.actualCost > p.budgetedCost")
    List<Project> findProjectsOverBudget();
    
    @Query("SELECT SUM(p.actualRevenue) FROM Project p WHERE p.status = 'COMPLETED'")
    BigDecimal getTotalCompletedRevenue();
    
    @Query("SELECT SUM(p.actualCost) FROM Project p")
    BigDecimal getTotalProjectCosts();
    
    @Query("SELECT p FROM Project p JOIN p.users u WHERE u.id = :userId")
    List<Project> findProjectsByUserId(@Param("userId") Long userId);
}
package com.sitemasterpro.repository;

import com.sitemasterpro.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {
    List<Project> findByStatus(Project.ProjectStatus status);
    
    @Query("SELECT p FROM Project p WHERE p.createdBy.id = ?1")
    List<Project> findByCreatedBy(Long userId);
    
    @Query("SELECT COUNT(p) FROM Project p WHERE p.status = ?1")
    Long countByStatus(Project.ProjectStatus status);
}
