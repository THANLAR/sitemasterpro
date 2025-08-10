package com.sitemasterpro.service;

import com.sitemasterpro.entity.InventoryTransaction;
import com.sitemasterpro.entity.Material;
import com.sitemasterpro.entity.Supplier;
import com.sitemasterpro.exception.CustomException;
import com.sitemasterpro.repository.InventoryTransactionRepository;
import com.sitemasterpro.repository.MaterialRepository;
import com.sitemasterpro.repository.SupplierRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class InventoryService {
    private static final Logger logger = LoggerFactory.getLogger(InventoryService.class);

    @Autowired
    private MaterialRepository materialRepository;

    @Autowired
    private SupplierRepository supplierRepository;

    @Autowired
    private InventoryTransactionRepository transactionRepository;

    @Autowired
    private AuditService auditService;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    // Material Management
    public Material createMaterial(Material material) {
        Material savedMaterial = materialRepository.save(material);
        logger.info("Material created: {}", savedMaterial.getName());
        
        auditService.logAction("CREATE_MATERIAL", "Material", savedMaterial.getId(), 
                              null, "Material created: " + savedMaterial.getName());
        
        return savedMaterial;
    }

    public Material updateMaterial(Material material) {
        Material existingMaterial = getMaterialById(material.getId());
        String oldValues = String.format("name: %s, unitPrice: %s, currentStock: %s", 
                                        existingMaterial.getName(), existingMaterial.getUnitPrice(), 
                                        existingMaterial.getCurrentStock());

        Material updatedMaterial = materialRepository.save(material);
        
        String newValues = String.format("name: %s, unitPrice: %s, currentStock: %s", 
                                        updatedMaterial.getName(), updatedMaterial.getUnitPrice(), 
                                        updatedMaterial.getCurrentStock());
        
        auditService.logAction("UPDATE_MATERIAL", "Material", material.getId(), oldValues, newValues);
        
        return updatedMaterial;
    }

    public Material getMaterialById(Long id) {
        return materialRepository.findById(id)
                .orElseThrow(() -> new CustomException("Material not found with id: " + id));
    }

    public List<Material> getAllMaterials() {
        return materialRepository.findAll();
    }

    public List<Material> getActiveMaterials() {
        return materialRepository.findByActiveTrue();
    }

    public List<Material> getLowStockMaterials() {
        return materialRepository.findLowStockMaterials();
    }

    // Supplier Management
    public Supplier createSupplier(Supplier supplier) {
        Supplier savedSupplier = supplierRepository.save(supplier);
        logger.info("Supplier created: {}", savedSupplier.getName());
        
        auditService.logAction("CREATE_SUPPLIER", "Supplier", savedSupplier.getId(), 
                              null, "Supplier created: " + savedSupplier.getName());
        
        return savedSupplier;
    }

    public Supplier updateSupplier(Supplier supplier) {
        Supplier existingSupplier = getSupplierById(supplier.getId());
        String oldValues = String.format("name: %s, contactPerson: %s, email: %s", 
                                        existingSupplier.getName(), existingSupplier.getContactPerson(), 
                                        existingSupplier.getEmail());

        Supplier updatedSupplier = supplierRepository.save(supplier);
        
        String newValues = String.format("name: %s, contactPerson: %s, email: %s", 
                                        updatedSupplier.getName(), updatedSupplier.getContactPerson(), 
                                        updatedSupplier.getEmail());
        
        auditService.logAction("UPDATE_SUPPLIER", "Supplier", supplier.getId(), oldValues, newValues);
        
        return updatedSupplier;
    }

    public Supplier getSupplierById(Long id) {
        return supplierRepository.findById(id)
                .orElseThrow(() -> new CustomException("Supplier not found with id: " + id));
    }

    public List<Supplier> getAllSuppliers() {
        return supplierRepository.findAll();
    }

    public List<Supplier> getActiveSuppliers() {
        return supplierRepository.findByActiveTrue();
    }

    // Inventory Transaction Management
    public InventoryTransaction recordStockIn(InventoryTransaction transaction) {
        validateTransaction(transaction);
        
        transaction.setType(InventoryTransaction.TransactionType.STOCK_IN);
        InventoryTransaction savedTransaction = transactionRepository.save(transaction);
        
        // Update material stock
        Material material = transaction.getMaterial();
        BigDecimal oldStock = material.getCurrentStock();
        material.setCurrentStock(oldStock.add(transaction.getQuantity()));
        materialRepository.save(material);
        
        auditService.logAction("STOCK_IN", "InventoryTransaction", savedTransaction.getId(), 
                              String.format("Material: %s, Old Stock: %s", material.getName(), oldStock),
                              String.format("Material: %s, New Stock: %s", material.getName(), material.getCurrentStock()));
        
        // Notify via WebSocket
        messagingTemplate.convertAndSend("/topic/inventory-updates", 
                String.format("Stock received: %s %s of %s", 
                            transaction.getQuantity(), material.getUnit(), material.getName()));
        
        logger.info("Stock in recorded: {} {} of {}", 
                   transaction.getQuantity(), material.getUnit(), material.getName());
        
        return savedTransaction;
    }

    public InventoryTransaction recordStockOut(InventoryTransaction transaction) {
        validateTransaction(transaction);
        
        Material material = transaction.getMaterial();
        
        // Check if sufficient stock is available
        if (material.getCurrentStock().compareTo(transaction.getQuantity()) < 0) {
            throw new CustomException("Insufficient stock. Available: " + material.getCurrentStock() + 
                                    " " + material.getUnit());
        }
        
        transaction.setType(InventoryTransaction.TransactionType.STOCK_OUT);
        InventoryTransaction savedTransaction = transactionRepository.save(transaction);
        
        // Update material stock
        BigDecimal oldStock = material.getCurrentStock();
        material.setCurrentStock(oldStock.subtract(transaction.getQuantity()));
        materialRepository.save(material);
        
        // Check for low stock alert
        if (material.isLowStock()) {
            notificationService.sendLowStockAlert(material);
        }
        
        auditService.logAction("STOCK_OUT", "InventoryTransaction", savedTransaction.getId(), 
                              String.format("Material: %s, Old Stock: %s", material.getName(), oldStock),
                              String.format("Material: %s, New Stock: %s", material.getName(), material.getCurrentStock()));
        
        // Notify via WebSocket
        messagingTemplate.convertAndSend("/topic/inventory-updates", 
                String.format("Stock issued: %s %s of %s to %s", 
                            transaction.getQuantity(), material.getUnit(), 
                            material.getName(), transaction.getIssuedTo()));
        
        logger.info("Stock out recorded: {} {} of {} to {}", 
                   transaction.getQuantity(), material.getUnit(), 
                   material.getName(), transaction.getIssuedTo());
        
        return savedTransaction;
    }

    public InventoryTransaction recordStockAdjustment(InventoryTransaction transaction, String reason) {
        validateTransaction(transaction);
        
        transaction.setType(InventoryTransaction.TransactionType.ADJUSTMENT);
        transaction.setNotes(reason);
        InventoryTransaction savedTransaction = transactionRepository.save(transaction);
        
        // Update material stock
        Material material = transaction.getMaterial();
        BigDecimal oldStock = material.getCurrentStock();
        
        // For adjustments, quantity can be positive (increase) or negative (decrease)
        material.setCurrentStock(oldStock.add(transaction.getQuantity()));
        materialRepository.save(material);
        
        auditService.logAction("STOCK_ADJUSTMENT", "InventoryTransaction", savedTransaction.getId(), 
                              String.format("Material: %s, Old Stock: %s, Reason: %s", 
                                          material.getName(), oldStock, reason),
                              String.format("Material: %s, New Stock: %s", 
                                          material.getName(), material.getCurrentStock()));
        
        logger.info("Stock adjustment recorded: {} {} of {} (Reason: {})", 
                   transaction.getQuantity(), material.getUnit(), material.getName(), reason);
        
        return savedTransaction;
    }

    private void validateTransaction(InventoryTransaction transaction) {
        if (transaction.getMaterial() == null) {
            throw new CustomException("Material is required for inventory transaction");
        }
        if (transaction.getQuantity() == null || transaction.getQuantity().compareTo(BigDecimal.ZERO) <= 0) {
            throw new CustomException("Quantity must be greater than zero");
        }
        if (transaction.getUnitPrice() == null || transaction.getUnitPrice().compareTo(BigDecimal.ZERO) < 0) {
            throw new CustomException("Unit price cannot be negative");
        }
    }

    public List<InventoryTransaction> getTransactionsByProject(Long projectId) {
        return transactionRepository.findByProjectId(projectId);
    }

    public List<InventoryTransaction> getTransactionsByMaterial(Long materialId) {
        return transactionRepository.findByMaterialId(materialId);
    }

    public List<InventoryTransaction> getTransactionsBySupplier(Long supplierId) {
        return transactionRepository.findBySupplierId(supplierId);
    }

    public List<InventoryTransaction> getTransactionsByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return transactionRepository.findByDateRange(startDate, endDate);
    }

    public BigDecimal calculateMaterialConsumptionCost(Long projectId, Long materialId) {
        List<InventoryTransaction> transactions = transactionRepository
                .findByProjectAndMaterial(projectId, materialId);
        
        return transactions.stream()
                .filter(t -> t.getType() == InventoryTransaction.TransactionType.STOCK_OUT)
                .map(InventoryTransaction::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public void generateLowStockReport() {
        List<Material> lowStockMaterials = getLowStockMaterials();
        
        if (!lowStockMaterials.isEmpty()) {
            logger.warn("Low stock alert for {} materials", lowStockMaterials.size());
            
            for (Material material : lowStockMaterials) {
                notificationService.sendLowStockAlert(material);
            }
            
            // Notify via WebSocket
            messagingTemplate.convertAndSend("/topic/alerts", 
                    String.format("Low stock alert: %d materials need restocking", lowStockMaterials.size()));
        }
    }
}
