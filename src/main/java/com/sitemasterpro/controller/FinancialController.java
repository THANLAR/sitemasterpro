package com.sitemasterpro.controller;

import com.sitemasterpro.dto.FinancialTransactionDto;
import com.sitemasterpro.entity.FinancialTransaction;
import com.sitemasterpro.entity.Project;
import com.sitemasterpro.service.FinancialService;
import com.sitemasterpro.service.ProjectService;
import com.sitemasterpro.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/api/financial")
public class FinancialController {

    @Autowired
    private FinancialService financialService;

    @Autowired
    private ProjectService projectService;

    @Autowired
    private UserService userService;

    @PostMapping("/transactions")
    @PreAuthorize("hasRole('ACCOUNTANT') or hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    @ResponseBody
    public ResponseEntity<FinancialTransaction> recordTransaction(@Valid @RequestBody FinancialTransactionDto dto) {
        FinancialTransaction transaction = convertToEntity(dto);
        FinancialTransaction savedTransaction = financialService.recordTransaction(transaction);
        return ResponseEntity.ok(savedTransaction);
    }

    @PutMapping("/transactions/{id}")
    @PreAuthorize("hasRole('ACCOUNTANT') or hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    @ResponseBody
    public ResponseEntity<FinancialTransaction> updateTransaction(@PathVariable Long id, 
                                                                 @Valid @RequestBody FinancialTransactionDto dto) {
        FinancialTransaction transaction = convertToEntity(dto);
        transaction.setId(id);
        FinancialTransaction updatedTransaction = financialService.updateTransaction(transaction);
        return ResponseEntity.ok(updatedTransaction);
    }

    @GetMapping("/transactions/{id}")
    @ResponseBody
    public ResponseEntity<FinancialTransaction> getTransaction(@PathVariable Long id) {
        FinancialTransaction transaction = financialService.getTransactionById(id);
        return ResponseEntity.ok(transaction);
    }

    @GetMapping("/transactions/project/{projectId}")
    @ResponseBody
    public ResponseEntity<List<FinancialTransaction>> getTransactionsByProject(@PathVariable Long projectId) {
        List<FinancialTransaction> transactions = financialService.getTransactionsByProject(projectId);
        return ResponseEntity.ok(transactions);
    }

    @GetMapping("/transactions/pending-approval")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN') or hasRole('CEO')")
    @ResponseBody
    public ResponseEntity<List<FinancialTransaction>> getPendingApprovalTransactions() {
        List<FinancialTransaction> transactions = financialService.getPendingApprovalTransactions();
        return ResponseEntity.ok(transactions);
    }

    @PostMapping("/transactions/{id}/approve")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    @ResponseBody
    public ResponseEntity<?> approveTransaction(@PathVariable Long id, @RequestParam Long approverId) {
        financialService.approveTransaction(id, approverId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/transactions/{id}/reject")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    @ResponseBody
    public ResponseEntity<?> rejectTransaction(@PathVariable Long id, @RequestParam String reason) {
        financialService.rejectTransaction(id, reason);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/project/{projectId}/revenue")
    @ResponseBody
    public ResponseEntity<BigDecimal> getProjectRevenue(@PathVariable Long projectId) {
        BigDecimal revenue = financialService.calculateProjectRevenue(projectId);
        return ResponseEntity.ok(revenue);
    }

    @GetMapping("/project/{projectId}/cost")
    @ResponseBody
    public ResponseEntity<BigDecimal> getProjectCost(@PathVariable Long projectId) {
        BigDecimal cost = financialService.calculateProjectCost(projectId);
        return ResponseEntity.ok(cost);
    }

    @GetMapping("/project/{projectId}/profit-margin")
    @ResponseBody
    public ResponseEntity<BigDecimal> getProjectProfitMargin(@PathVariable Long projectId) {
        BigDecimal profitMargin = financialService.calculateProjectProfitMargin(projectId);
        return ResponseEntity.ok(profitMargin);
    }

    @GetMapping("/project/{projectId}/expenses-by-category")
    @ResponseBody
    public ResponseEntity<Map<FinancialTransaction.Category, BigDecimal>> getExpensesByCategory(@PathVariable Long projectId) {
        Map<FinancialTransaction.Category, BigDecimal> expenses = financialService.getExpensesByCategory(projectId);
        return ResponseEntity.ok(expenses);
    }

    @GetMapping("/project/{projectId}/budget-variance")
    @ResponseBody
    public ResponseEntity<BigDecimal> getBudgetVariance(@PathVariable Long projectId) {
        BigDecimal variance = financialService.calculateBudgetVariance(projectId);
        return ResponseEntity.ok(variance);
    }

    @GetMapping("/projects/over-budget")
    @PreAuthorize("hasRole('CEO') or hasRole('ADMIN') or hasRole('SUPER_ADMIN') or hasRole('ACCOUNTANT')")
    @ResponseBody
    public ResponseEntity<List<Project>> getProjectsOverBudget() {
        List<Project> projects = financialService.getProjectsOverBudget();
        return ResponseEntity.ok(projects);
    }

    @GetMapping("/transactions/daterange")
    @ResponseBody
    public ResponseEntity<List<FinancialTransaction>> getTransactionsByDateRange(
            @RequestParam String startDate, @RequestParam String endDate) {
        LocalDateTime start = LocalDateTime.parse(startDate, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        LocalDateTime end = LocalDateTime.parse(endDate, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        List<FinancialTransaction> transactions = financialService.getTransactionsByDateRange(start, end);
        return ResponseEntity.ok(transactions);
    }

    // Thymeleaf views
    @GetMapping("/view")
    public String financialView(Model model) {
        List<FinancialTransaction> pendingTransactions = financialService.getPendingApprovalTransactions();
        List<Project> overBudgetProjects = financialService.getProjectsOverBudget();
        
        model.addAttribute("pendingTransactions", pendingTransactions);
        model.addAttribute("overBudgetProjects", overBudgetProjects);
        model.addAttribute("projects", projectService.getAllProjects());
        return "financial";
    }

    @GetMapping("/transactions/view")
    public String transactionsView(Model model) {
        // Load recent transactions
        LocalDateTime endDate = LocalDateTime.now();
        LocalDateTime startDate = endDate.minusDays(30);
        List<FinancialTransaction> recentTransactions = financialService.getTransactionsByDateRange(startDate, endDate);
        
        model.addAttribute("transactions", recentTransactions);
        model.addAttribute("projects", projectService.getAllProjects());
        model.addAttribute("transactionTypes", FinancialTransaction.TransactionType.values());
        model.addAttribute("categories", FinancialTransaction.Category.values());
        return "financial/transactions";
    }

    private FinancialTransaction convertToEntity(FinancialTransactionDto dto) {
        FinancialTransaction transaction = new FinancialTransaction();
        
        if (dto.getProjectId() != null) {
            transaction.setProject(projectService.getProjectById(dto.getProjectId()));
        }
        if (dto.getCreatedById() != null) {
            transaction.setCreatedBy(userService.getUserById(dto.getCreatedById()));
        }
        
        transaction.setType(dto.getType());
        transaction.setCategory(dto.getCategory());
        transaction.setAmount(dto.getAmount());
        transaction.setDescription(dto.getDescription());
        transaction.setReferenceNumber(dto.getReferenceNumber());
        transaction.setNotes(dto.getNotes());
        
        if (dto.getTransactionDate() != null) {
            transaction.setTransactionDate(dto.getTransactionDate());
        }
        
        return transaction;
    }
}
