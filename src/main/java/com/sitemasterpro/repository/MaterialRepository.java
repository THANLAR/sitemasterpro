package com.sitemasterpro.repository;

import com.sitemasterpro.entity.Material;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MaterialRepository extends JpaRepository<Material, Long> {
    List<Material> findByActiveTrue();
    
    @Query("SELECT m FROM Material m WHERE m.currentStock <= m.minStockLevel AND m.active = true")
    List<Material> findLowStockMaterials();
    
    @Query("SELECT m FROM Material m WHERE m.name LIKE %:name% AND m.active = true")
    List<Material> findByNameContainingIgnoreCase(String name);
    
    List<Material> findByUnit(String unit);
}
