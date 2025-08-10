package com.sitemasterpro.controller;

import com.sitemasterpro.dto.InventoryTransactionDto;
import com.sitemasterpro.entity.InventoryTransaction;
import com.sitemasterpro.entity.Material;
import com.sitemasterpro.entity.Supplier;
import com.sitemasterpro.service.InventoryService;
import com.sitemasterpro.service.ProjectService;
import com.sitemasterpro.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Controller
@RequestMapping("/api/inventory")
public class InventoryController {

    @Autowired
    private InventoryService inventoryService;

    @Autowired
    private ProjectService projectService;

    @Autowired
    private UserService userService;

    // Material Management
    @GetMapping("/materials")
    @ResponseBody
    public ResponseEntity<List<Material>> getAllMaterials() {
        List<Material> materials = inventoryService.getActiveMaterials();
        return ResponseEntity.ok(materials);
    }

    @PostMapping("/materials")
    @PreAuthorize("hasRole('STORE_KEEPER') or hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    @ResponseBody
    public ResponseEntity<Material> createMaterial(@Valid @RequestBody Material material) {
        Material savedMaterial = inventoryService.createMaterial(material);
        return ResponseEntity.ok(savedMaterial);
    }

    @PutMapping("/materials/{id}")
    @PreAuthorize("hasRole('STORE_KEEPER') or hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    @ResponseBody
    public ResponseEntity<Material> updateMaterial(@PathVariable Long id, @Valid @RequestBody Material material) {
        material.setId(id);
        Material updatedMaterial = inventoryService.updateMaterial(material);
        return ResponseEntity.ok(updatedMaterial);
    }

    @GetMapping("/materials/low-stock")
    @PreAuthorize("hasRole('STORE_KEEPER') or hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    @ResponseBody
    public ResponseEntity<List<Material>> getLowStockMaterials() {
        List<Material> lowStockMaterials = inventoryService.getLowStockMaterials();
        return ResponseEntity.ok(lowStockMaterials);
    }

    // Supplier Management
    @GetMapping("/suppliers")
    @ResponseBody
    public ResponseEntity<List<Supplier>> getAllSuppliers() {
        List<Supplier> suppliers = inventoryService.getActiveSuppliers();
        return ResponseEntity.ok(suppliers);
    }

    @PostMapping("/suppliers")
    @PreAuthorize("hasRole('STORE_KEEPER') or hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    @ResponseBody
    public ResponseEntity<Supplier> createSupplier(@Valid @RequestBody Supplier supplier) {
        Supplier savedSupplier = inventoryService.createSupplier(supplier);
        return ResponseEntity.ok(savedSupplier);
    }

    @PutMapping("/suppliers/{id}")
    @PreAuthorize("hasRole('STORE_KEEPER') or hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    @ResponseBody
    public ResponseEntity<Supplier> updateSupplier(@PathVariable Long id, @Valid @RequestBody Supplier supplier) {
        supplier.setId(id);
        Supplier updatedSupplier = inventoryService.updateSupplier(supplier);
        return ResponseEntity.ok(updatedSupplier);
    }

    // Transaction Management
    @PostMapping("/stock-in")
    @PreAuthorize("hasRole('STORE_KEEPER') or hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    @ResponseBody
    public ResponseEntity<InventoryTransaction> recordStockIn(@Valid @RequestBody InventoryTransactionDto dto) {
        InventoryTransaction transaction = convertToEntity(dto);
        InventoryTransaction savedTransaction = inventoryService.recordStockIn(transaction);
        return ResponseEntity.ok(savedTransaction);
    }

    @PostMapping("/stock-out")
    @PreAuthorize("hasRole('STORE_KEEPER') or hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    @ResponseBody
    public ResponseEntity<InventoryTransaction> recordStockOut(@Valid @RequestBody InventoryTransactionDto dto) {
        InventoryTransaction transaction = convertToEntity(dto);
        InventoryTransaction savedTransaction = inventoryService.recordStockOut(transaction);
        return ResponseEntity.ok(savedTransaction);
    }

    @PostMapping("/adjustment")
    @PreAuthorize("hasRole('STORE_KEEPER') or hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    @ResponseBody
    public ResponseEntity<InventoryTransaction> recordStockAdjustment(@Valid @RequestBody InventoryTransactionDto dto,
                                                                     @RequestParam String reason) {
        InventoryTransaction transaction = convertToEntity(dto);
        InventoryTransaction savedTransaction = inventoryService.recordStockAdjustment(transaction, reason);
        return ResponseEntity.ok(savedTransaction);
    }

    @GetMapping("/transactions/project/{projectId}")
    @ResponseBody
    public ResponseEntity<List<InventoryTransaction>> getTransactionsByProject(@PathVariable Long projectId) {
        List<InventoryTransaction> transactions = inventoryService.getTransactionsByProject(projectId);
        return ResponseEntity.ok(transactions);
    }

    @GetMapping("/transactions/material/{materialId}")
    @ResponseBody
    public ResponseEntity<List<InventoryTransaction>> getTransactionsByMaterial(@PathVariable Long materialId) {
        List<InventoryTransaction> transactions = inventoryService.getTransactionsByMaterial(materialId);
        return ResponseEntity.ok(transactions);
    }

    @GetMapping("/transactions/daterange")
    @ResponseBody
    public ResponseEntity<List<InventoryTransaction>> getTransactionsByDateRange(
            @RequestParam String startDate, @RequestParam String endDate) {
        LocalDateTime start = LocalDateTime.parse(startDate, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        LocalDateTime end = LocalDateTime.parse(endDate, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        List<InventoryTransaction> transactions = inventoryService.getTransactionsByDateRange(start, end);
        return ResponseEntity.ok(transactions);
    }

    // Thymeleaf views
    @GetMapping("/view")
    public String inventoryView(Model model) {
        List<Material> materials = inventoryService.getActiveMaterials();
        List<Supplier> suppliers = inventoryService.getActiveSuppliers();
        List<Material> lowStockMaterials = inventoryService.getLowStockMaterials();
        
        model.addAttribute("materials", materials);
        model.addAttribute("suppliers", suppliers);
        model.addAttribute("lowStockMaterials", lowStockMaterials);
        return "inventory";
    }

    @GetMapping("/materials/view")
    public String materialsView(Model model) {
        List<Material> materials = inventoryService.getActiveMaterials();
        model.addAttribute("materials", materials);
        return "inventory/materials";
    }

    @GetMapping("/transactions/view")
    public String transactionsView(Model model) {
        // Load recent transactions for display
        LocalDateTime endDate = LocalDateTime.now();
        LocalDateTime startDate = endDate.minusDays(30); // Last 30 days
        List<InventoryTransaction> recentTransactions = inventoryService.getTransactionsByDateRange(startDate, endDate);
        
        model.addAttribute("transactions", recentTransactions);
        model.addAttribute("materials", inventoryService.getActiveMaterials());
        model.addAttribute("suppliers", inventoryService.getActiveSuppliers());
        model.addAttribute("projects", projectService.getAllProjects());
        return "inventory/transactions";
    }

    private InventoryTransaction convertToEntity(InventoryTransactionDto dto) {
        InventoryTransaction transaction = new InventoryTransaction();
        
        if (dto.getProjectId() != null) {
            transaction.setProject(projectService.getProjectById(dto.getProjectId()));
        }
        if (dto.getMaterialId() != null) {
            transaction.setMaterial(inventoryService.getMaterialById(dto.getMaterialId()));
        }
        if (dto.getSupplierId() != null) {
            transaction.setSupplier(inventoryService.getSupplierById(dto.getSupplierId()));
        }
        if (dto.getCreatedById() != null) {
            transaction.setCreatedBy(userService.getUserById(dto.getCreatedById()));
        }
        
        transaction.setQuantity(dto.getQuantity());
        transaction.setUnitPrice(dto.getUnitPrice());
        transaction.setPurchaseOrderReference(dto.getPurchaseOrderReference());
        transaction.setIssuedTo(dto.getIssuedTo());
        transaction.setNotes(dto.getNotes());
        
        if (dto.getTransactionDate() != null) {
            transaction.setTransactionDate(dto.getTransactionDate());
        }
        
        return transaction;
    }
}
