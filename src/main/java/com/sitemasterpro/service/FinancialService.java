package com.sitemasterpro.service;

import com.sitemasterpro.entity.FinancialTransaction;
import com.sitemasterpro.entity.Project;
import com.sitemasterpro.exception.CustomException;
import com.sitemasterpro.repository.FinancialTransactionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
public class FinancialService {
    private static final Logger logger = LoggerFactory.getLogger(FinancialService.class);

    @Autowired
    private FinancialTransactionRepository transactionRepository;

    @Autowired
    private ProjectService projectService;

    @Autowired
    private AuditService auditService;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    public FinancialTransaction recordTransaction(FinancialTransaction transaction) {
        validateTransaction(transaction);
        
        FinancialTransaction savedTransaction = transactionRepository.save(transaction);
        
        auditService.logAction("CREATE_FINANCIAL_TRANSACTION", "FinancialTransaction", 
                              savedTransaction.getId(), null, 
                              String.format("Type: %s, Category: %s, Amount: %s", 
                                          transaction.getType(), transaction.getCategory(), 
                                          transaction.getAmount()));
        
        // Update project financials
        updateProjectFinancialsFromTransaction(savedTransaction);
        
        // Notify via WebSocket
        messagingTemplate.convertAndSend("/topic/financial-updates", 
                String.format("New %s transaction: %s - %s", 
                            transaction.getType(), transaction.getAmount(), 
                            transaction.getDescription()));
        
        logger.info("Financial transaction recorded: {} {} for project {}", 
                   transaction.getType(), transaction.getAmount(), 
                   transaction.getProject().getName());
        
        return savedTransaction;
    }

    public FinancialTransaction updateTransaction(FinancialTransaction transaction) {
        FinancialTransaction existingTransaction = getTransactionById(transaction.getId());
        String oldValues = String.format("amount: %s, description: %s, approved: %s", 
                                        existingTransaction.getAmount(), 
                                        existingTransaction.getDescription(),
                                        existingTransaction.getApproved());

        FinancialTransaction updatedTransaction = transactionRepository.save(transaction);
        
        String newValues = String.format("amount: %s, description: %s, approved: %s", 
                                        updatedTransaction.getAmount(), 
                                        updatedTransaction.getDescription(),
                                        updatedTransaction.getApproved());
        
        auditService.logAction("UPDATE_FINANCIAL_TRANSACTION", "FinancialTransaction", 
                              transaction.getId(), oldValues, newValues);
        
        // Update project financials if approval status changed
        if (!existingTransaction.getApproved().equals(updatedTransaction.getApproved())) {
            updateProjectFinancialsFromTransaction(updatedTransaction);
        }
        
        return updatedTransaction;
    }

    public FinancialTransaction getTransactionById(Long id) {
        return transactionRepository.findById(id)
                .orElseThrow(() -> new CustomException("Financial transaction not found with id: " + id));
    }

    public List<FinancialTransaction> getTransactionsByProject(Long projectId) {
        return transactionRepository.findByProjectId(projectId);
    }

    public List<FinancialTransaction> getPendingApprovalTransactions() {
        return transactionRepository.findByApprovedFalse();
    }

    public List<FinancialTransaction> getApprovedTransactions() {
        return transactionRepository.findByApprovedTrue();
    }

    public void approveTransaction(Long transactionId, Long approverId) {
        FinancialTransaction transaction = getTransactionById(transactionId);
        
        if (transaction.getApproved()) {
            throw new CustomException("Transaction is already approved");
        }
        
        transaction.setApproved(true);
        transaction.setApprovedAt(LocalDateTime.now());
        // Note: You would need to fetch the approver user here
        // transaction.setApprovedBy(userService.getUserById(approverId));
        
        transactionRepository.save(transaction);
        
        // Update project financials
        updateProjectFinancialsFromTransaction(transaction);
        
        auditService.logAction("APPROVE_TRANSACTION", "FinancialTransaction", transactionId, 
                              "approved: false", "approved: true");
        
        // Notify via WebSocket
        messagingTemplate.convertAndSend("/topic/financial-updates", 
                String.format("Transaction approved: %s - %s", 
                            transaction.getAmount(), transaction.getDescription()));
        
        logger.info("Financial transaction approved: {} for project {}", 
                   transaction.getId(), transaction.getProject().getName());
    }

    public void rejectTransaction(Long transactionId, String reason) {
        FinancialTransaction transaction = getTransactionById(transactionId);
        transaction.setNotes(transaction.getNotes() + "\nREJECTED: " + reason);
        transactionRepository.save(transaction);
        
        auditService.logAction("REJECT_TRANSACTION", "FinancialTransaction", transactionId, 
                              null, "Transaction rejected: " + reason);
        
        logger.info("Financial transaction rejected: {} - Reason: {}", transaction.getId(), reason);
    }

    private void updateProjectFinancialsFromTransaction(FinancialTransaction transaction) {
        if (!transaction.getApproved()) {
            return; // Only approved transactions affect project financials
        }

        Project project = transaction.getProject();
        
        if (transaction.getType() == FinancialTransaction.TransactionType.INCOME) {
            BigDecimal newRevenue = calculateProjectRevenue(project.getId());
            project.setActualRevenue(newRevenue);
        } else {
            BigDecimal newCost = calculateProjectCost(project.getId());
            project.setActualCost(newCost);
        }
        
        projectService.updateProject(project);
    }

    private void validateTransaction(FinancialTransaction transaction) {
        if (transaction.getProject() == null) {
            throw new CustomException("Project is required for financial transaction");
        }
        if (transaction.getAmount() == null || transaction.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new CustomException("Transaction amount must be greater than zero");
        }
        if (transaction.getDescription() == null || transaction.getDescription().trim().isEmpty()) {
            throw new CustomException("Transaction description is required");
        }
    }

    public BigDecimal calculateProjectRevenue(Long projectId) {
        BigDecimal revenue = transactionRepository.sumAmountByProjectAndType(
                projectId, FinancialTransaction.TransactionType.INCOME);
        return revenue != null ? revenue : BigDecimal.ZERO;
    }

    public BigDecimal calculateProjectCost(Long projectId) {
        BigDecimal cost = transactionRepository.sumAmountByProjectAndType(
                projectId, FinancialTransaction.TransactionType.EXPENSE);
        return cost != null ? cost : BigDecimal.ZERO;
    }

    public BigDecimal calculateProjectProfitMargin(Long projectId) {
        BigDecimal revenue = calculateProjectRevenue(projectId);
        BigDecimal cost = calculateProjectCost(projectId);
        
        if (revenue.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        
        BigDecimal profit = revenue.subtract(cost);
        return profit.divide(revenue, 4, BigDecimal.ROUND_HALF_UP).multiply(BigDecimal.valueOf(100));
    }

    public Map<FinancialTransaction.Category, BigDecimal> getExpensesByCategory(Long projectId) {
        List<FinancialTransaction> expenses = transactionRepository.findByProjectId(projectId)
                .stream()
                .filter(t -> t.getType() == FinancialTransaction.TransactionType.EXPENSE)
                .filter(FinancialTransaction::getApproved)
                .collect(Collectors.toList());

        return expenses.stream()
                .collect(Collectors.groupingBy(
                        FinancialTransaction::getCategory,
                        Collectors.reducing(BigDecimal.ZERO, 
                                          FinancialTransaction::getAmount, 
                                          BigDecimal::add)
                ));
    }

    public BigDecimal calculateCostByCategory(Long projectId, FinancialTransaction.Category category) {
        BigDecimal cost = transactionRepository.sumAmountByProjectAndCategory(projectId, category);
        return cost != null ? cost : BigDecimal.ZERO;
    }

    public List<FinancialTransaction> getTransactionsByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return transactionRepository.findByDateRange(startDate, endDate);
    }

    public BigDecimal calculateBudgetVariance(Long projectId) {
        Project project = projectService.getProjectById(projectId);
        BigDecimal actualCost = calculateProjectCost(projectId);
        
        if (project.getBudgetedCost().compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        
        BigDecimal variance = actualCost.subtract(project.getBudgetedCost());
        return variance.divide(project.getBudgetedCost(), 4, BigDecimal.ROUND_HALF_UP)
                      .multiply(BigDecimal.valueOf(100));
    }

    public boolean isProjectOverBudget(Long projectId) {
        return calculateBudgetVariance(projectId).compareTo(BigDecimal.ZERO) > 0;
    }

    public List<Project> getProjectsOverBudget() {
        return projectService.getAllProjects().stream()
                .filter(project -> isProjectOverBudget(project.getId()))
                .collect(Collectors.toList());
    }
}
