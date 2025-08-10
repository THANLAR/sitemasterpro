package com.sitemasterpro.service;

import com.sitemasterpro.entity.*;
import com.sitemasterpro.repository.*;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ReportService {
    private static final Logger logger = LoggerFactory.getLogger(ReportService.class);

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private FinancialTransactionRepository financialTransactionRepository;

    @Autowired
    private InventoryTransactionRepository inventoryTransactionRepository;

    @Autowired
    private MaterialRepository materialRepository;

    @Autowired
    private LaborRecordRepository laborRecordRepository;

    @Autowired
    private FinancialService financialService;

    public byte[] generateProjectFinancialReport(Long projectId) throws IOException {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Project Financial Report");

        // Create header style
        CellStyle headerStyle = workbook.createCellStyle();
        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerStyle.setFont(headerFont);

        // Title
        Row titleRow = sheet.createRow(0);
        Cell titleCell = titleRow.createCell(0);
        titleCell.setCellValue("Project Financial Report: " + project.getName());
        titleCell.setCellStyle(headerStyle);

        // Project summary
        int rowNum = 2;
        createRow(sheet, rowNum++, "Contract Value:", project.getContractValue().toString());
        createRow(sheet, rowNum++, "Budgeted Cost:", project.getBudgetedCost().toString());
        createRow(sheet, rowNum++, "Actual Cost:", project.getActualCost().toString());
        createRow(sheet, rowNum++, "Actual Revenue:", project.getActualRevenue().toString());
        createRow(sheet, rowNum++, "Profit Margin:", project.calculateProfitMargin().toString() + "%");
        createRow(sheet, rowNum++, "Completion:", project.getCompletionPercentage().toString() + "%");

        // Financial transactions
        rowNum += 2;
        Row transHeaderRow = sheet.createRow(rowNum++);
        transHeaderRow.createCell(0).setCellValue("Date");
        transHeaderRow.createCell(1).setCellValue("Type");
        transHeaderRow.createCell(2).setCellValue("Category");
        transHeaderRow.createCell(3).setCellValue("Amount");
        transHeaderRow.createCell(4).setCellValue("Description");

        List<FinancialTransaction> transactions = financialTransactionRepository.findByProjectId(projectId);
        for (FinancialTransaction transaction : transactions) {
            Row dataRow = sheet.createRow(rowNum++);
            dataRow.createCell(0).setCellValue(transaction.getTransactionDate().toString());
            dataRow.createCell(1).setCellValue(transaction.getType().toString());
            dataRow.createCell(2).setCellValue(transaction.getCategory().toString());
            dataRow.createCell(3).setCellValue(transaction.getAmount().doubleValue());
            dataRow.createCell(4).setCellValue(transaction.getDescription());
        }

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        workbook.write(outputStream);
        workbook.close();

        logger.info("Project financial report generated for project: {}", project.getName());
        return outputStream.toByteArray();
    }

    public byte[] generateInventoryReport() throws IOException {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Inventory Report");

        // Create header style
        CellStyle headerStyle = workbook.createCellStyle();
        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerStyle.setFont(headerFont);

        // Title
        Row titleRow = sheet.createRow(0);
        Cell titleCell = titleRow.createCell(0);
        titleCell.setCellValue("Inventory Report");
        titleCell.setCellStyle(headerStyle);

        // Headers
        int rowNum = 2;
        Row headerRow = sheet.createRow(rowNum++);
        headerRow.createCell(0).setCellValue("Material Name");
        headerRow.createCell(1).setCellValue("Current Stock");
        headerRow.createCell(2).setCellValue("Unit");
        headerRow.createCell(3).setCellValue("Unit Price");
        headerRow.createCell(4).setCellValue("Total Value");
        headerRow.createCell(5).setCellValue("Min Stock Level");
        headerRow.createCell(6).setCellValue("Status");

        List<Material> materials = materialRepository.findByActiveTrue();
        BigDecimal totalInventoryValue = BigDecimal.ZERO;

        for (Material material : materials) {
            Row dataRow = sheet.createRow(rowNum++);
            dataRow.createCell(0).setCellValue(material.getName());
            dataRow.createCell(1).setCellValue(material.getCurrentStock().doubleValue());
            dataRow.createCell(2).setCellValue(material.getUnit());
            dataRow.createCell(3).setCellValue(material.getUnitPrice().doubleValue());
            
            BigDecimal totalValue = material.getCurrentStock().multiply(material.getUnitPrice());
            dataRow.createCell(4).setCellValue(totalValue.doubleValue());
            dataRow.createCell(5).setCellValue(material.getMinStockLevel().doubleValue());
            dataRow.createCell(6).setCellValue(material.isLowStock() ? "LOW STOCK" : "OK");
            
            totalInventoryValue = totalInventoryValue.add(totalValue);
        }

        // Total row
        rowNum++;
        Row totalRow = sheet.createRow(rowNum);
        totalRow.createCell(3).setCellValue("Total Inventory Value:");
        totalRow.createCell(4).setCellValue(totalInventoryValue.doubleValue());

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        workbook.write(outputStream);
        workbook.close();

        logger.info("Inventory report generated");
        return outputStream.toByteArray();
    }

    public byte[] generateLaborReport(Long projectId) throws IOException {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Labor Report");

        // Create header style
        CellStyle headerStyle = workbook.createCellStyle();
        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerStyle.setFont(headerFont);

        // Title
        Row titleRow = sheet.createRow(0);
        Cell titleCell = titleRow.createCell(0);
        titleCell.setCellValue("Labor Report");
        titleCell.setCellStyle(headerStyle);

        // Headers
        int rowNum = 2;
        Row headerRow = sheet.createRow(rowNum++);
        headerRow.createCell(0).setCellValue("Worker Name");
        headerRow.createCell(1).setCellValue("Job Title");
        headerRow.createCell(2).setCellValue("Work Date");
        headerRow.createCell(3).setCellValue("Hours Worked");
        headerRow.createCell(4).setCellValue("Overtime Hours");
        headerRow.createCell(5).setCellValue("Total Pay");

        List<LaborRecord> laborRecords = laborRecordRepository.findByProjectIdOrderByWorkDateDesc(projectId);
        BigDecimal totalPay = BigDecimal.ZERO;

        for (LaborRecord record : laborRecords) {
            Row dataRow = sheet.createRow(rowNum++);
            dataRow.createCell(0).setCellValue(record.getWorkerName());
            dataRow.createCell(1).setCellValue(record.getJobTitle());
            dataRow.createCell(2).setCellValue(record.getWorkDate().toString());
            dataRow.createCell(3).setCellValue(record.getHoursWorked().doubleValue());
            dataRow.createCell(4).setCellValue(record.getOvertimeHours() != null ? record.getOvertimeHours().doubleValue() : 0);
            dataRow.createCell(5).setCellValue(record.getTotalPay().doubleValue());
            
            totalPay = totalPay.add(record.getTotalPay());
        }

        // Total row
        rowNum++;
        Row totalRow = sheet.createRow(rowNum);
        totalRow.createCell(4).setCellValue("Total Labor Cost:");
        totalRow.createCell(5).setCellValue(totalPay.doubleValue());

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        workbook.write(outputStream);
        workbook.close();

        logger.info("Labor report generated for project ID: {}", projectId);
        return outputStream.toByteArray();
    }

    public Map<String, Object> generateDashboardData() {
        List<Project> allProjects = projectRepository.findAll();
        List<Project> activeProjects = projectRepository.findByStatus(Project.ProjectStatus.IN_PROGRESS);
        List<Material> lowStockMaterials = materialRepository.findLowStockMaterials();
        List<FinancialTransaction> pendingApprovals = financialTransactionRepository.findByApprovedFalse();

        BigDecimal totalRevenue = allProjects.stream()
                .map(Project::getActualRevenue)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalCost = allProjects.stream()
                .map(Project::getActualCost)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return Map.of(
            "totalProjects", allProjects.size(),
            "activeProjects", activeProjects.size(),
            "totalRevenue", totalRevenue,
            "totalCost", totalCost,
            "profitMargin", calculateOverallProfitMargin(totalRevenue, totalCost),
            "lowStockItems", lowStockMaterials.size(),
            "pendingApprovals", pendingApprovals.size(),
            "recentProjects", allProjects.stream().limit(5).collect(Collectors.toList()),
            "overdueProjects", projectRepository.findOverdueProjects(java.time.LocalDate.now()).size()
        );
    }

    private BigDecimal calculateOverallProfitMargin(BigDecimal revenue, BigDecimal cost) {
        if (revenue.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        return revenue.subtract(cost).divide(revenue, 4, BigDecimal.ROUND_HALF_UP).multiply(BigDecimal.valueOf(100));
    }

    private void createRow(Sheet sheet, int rowNum, String label, String value) {
        Row row = sheet.createRow(rowNum);
        row.createCell(0).setCellValue(label);
        row.createCell(1).setCellValue(value);
    }

    public byte[] generateAuditReport(LocalDateTime startDate, LocalDateTime endDate) throws IOException {
        // Implementation for audit report generation
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Audit Report");

        Row titleRow = sheet.createRow(0);
        titleRow.createCell(0).setCellValue("Audit Report");

        // Add audit data rows here
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        workbook.write(outputStream);
        workbook.close();

        return outputStream.toByteArray();
    }
}
