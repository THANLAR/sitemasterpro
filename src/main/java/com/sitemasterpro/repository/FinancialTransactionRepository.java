package com.sitemasterpro.repository;

import com.sitemasterpro.entity.FinancialTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface FinancialTransactionRepository extends JpaRepository<FinancialTransaction, Long> {
    List<FinancialTransaction> findByProjectId(Long projectId);
    
    List<FinancialTransaction> findByType(FinancialTransaction.TransactionType type);
    
    List<FinancialTransaction> findByCategory(FinancialTransaction.Category category);
    
    List<FinancialTransaction> findByApprovedTrue();
    
    List<FinancialTransaction> findByApprovedFalse();
    
    @Query("SELECT ft FROM FinancialTransaction ft WHERE ft.transactionDate BETWEEN :startDate AND :endDate")
    List<FinancialTransaction> findByDateRange(@Param("startDate") LocalDateTime startDate, 
                                             @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT SUM(ft.amount) FROM FinancialTransaction ft WHERE ft.project.id = :projectId AND ft.type = :type AND ft.approved = true")
    BigDecimal sumAmountByProjectAndType(@Param("projectId") Long projectId, 
                                       @Param("type") FinancialTransaction.TransactionType type);
    
    @Query("SELECT SUM(ft.amount) FROM FinancialTransaction ft WHERE ft.project.id = :projectId AND ft.category = :category AND ft.approved = true")
    BigDecimal sumAmountByProjectAndCategory(@Param("projectId") Long projectId, 
                                           @Param("category") FinancialTransaction.Category category);
}
