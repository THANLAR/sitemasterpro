package com.sitemasterpro.repository;

import com.sitemasterpro.entity.LaborRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface LaborRecordRepository extends JpaRepository<LaborRecord, Long> {
    
    List<LaborRecord> findByProjectIdOrderByWorkDateDesc(Long projectId);
    
    List<LaborRecord> findByWorkDate(LocalDate workDate);
    
    @Query("SELECT lr FROM LaborRecord lr WHERE lr.workDate BETWEEN :startDate AND :endDate ORDER BY lr.workDate DESC")
    List<LaborRecord> findByDateRange(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
    
    @Query("SELECT lr FROM LaborRecord lr WHERE lr.project.id = :projectId AND lr.workDate BETWEEN :startDate AND :endDate ORDER BY lr.workDate DESC")
    List<LaborRecord> findByProjectAndDateRange(@Param("projectId") Long projectId, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
    
    @Query("SELECT SUM(lr.totalPay) FROM LaborRecord lr WHERE lr.project.id = :projectId")
    BigDecimal sumTotalPayByProject(@Param("projectId") Long projectId);
    
    @Query("SELECT SUM(lr.hoursWorked) FROM LaborRecord lr WHERE lr.project.id = :projectId AND lr.workDate BETWEEN :startDate AND :endDate")
    BigDecimal sumHoursWorkedByProjectAndDateRange(@Param("projectId") Long projectId, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
    
    List<LaborRecord> findByWorkerNameContainingIgnoreCaseOrderByWorkDateDesc(String workerName);
}
