package com.sitemasterpro.repository;

import com.sitemasterpro.entity.InventoryTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface InventoryTransactionRepository extends JpaRepository<InventoryTransaction, Long> {
    List<InventoryTransaction> findByProjectId(Long projectId);
    
    List<InventoryTransaction> findByMaterialId(Long materialId);
    
    List<InventoryTransaction> findBySupplierId(Long supplierId);
    
    List<InventoryTransaction> findByType(InventoryTransaction.TransactionType type);
    
    @Query("SELECT it FROM InventoryTransaction it WHERE it.transactionDate BETWEEN :startDate AND :endDate")
    List<InventoryTransaction> findByDateRange(@Param("startDate") LocalDateTime startDate, 
                                             @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT it FROM InventoryTransaction it WHERE it.project.id = :projectId AND it.material.id = :materialId")
    List<InventoryTransaction> findByProjectAndMaterial(@Param("projectId") Long projectId, 
                                                       @Param("materialId") Long materialId);
}
