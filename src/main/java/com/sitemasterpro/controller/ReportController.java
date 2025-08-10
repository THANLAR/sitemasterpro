package com.sitemasterpro.controller;

import com.sitemasterpro.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Controller
@RequestMapping("/api/reports")
public class ReportController {

    @Autowired
    private ReportService reportService;

    @GetMapping("/project/{projectId}/financial")
    @PreAuthorize("hasRole('CEO') or hasRole('ADMIN') or hasRole('SUPER_ADMIN') or hasRole('ACCOUNTANT')")
    public ResponseEntity<ByteArrayResource> generateProjectFinancialReport(@PathVariable Long projectId) throws IOException {
        byte[] reportData = reportService.generateProjectFinancialReport(projectId);
        
        ByteArrayResource resource = new ByteArrayResource(reportData);
        
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=project-financial-report-" + projectId + ".xlsx")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .contentLength(reportData.length)
                .body(resource);
    }

    @GetMapping("/inventory")
    @PreAuthorize("hasRole('STORE_KEEPER') or hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<ByteArrayResource> generateInventoryReport() throws IOException {
        byte[] reportData = reportService.generateInventoryReport();
        
        ByteArrayResource resource = new ByteArrayResource(reportData);
        
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=inventory-report.xlsx")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .contentLength(reportData.length)
                .body(resource);
    }

    @GetMapping("/labor/{projectId}")
    @PreAuthorize("hasRole('LABOR_HEAD') or hasRole('SITE_MANAGER') or hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<ByteArrayResource> generateLaborReport(@PathVariable Long projectId) throws IOException {
        byte[] reportData = reportService.generateLaborReport(projectId);
        
        ByteArrayResource resource = new ByteArrayResource(reportData);
        
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=labor-report-" + projectId + ".xlsx")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .contentLength(reportData.length)
                .body(resource);
    }

    @GetMapping("/audit")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<ByteArrayResource> generateAuditReport(
            @RequestParam String startDate, @RequestParam String endDate) throws IOException {
        LocalDateTime start = LocalDateTime.parse(startDate, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        LocalDateTime end = LocalDateTime.parse(endDate, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        
        byte[] reportData = reportService.generateAuditReport(start, end);
        
        ByteArrayResource resource = new ByteArrayResource(reportData);
        
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=audit-report.xlsx")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .contentLength(reportData.length)
                .body(resource);
    }

    @GetMapping("/view")
    public String reportsView(Model model) {
        return "reports";
    }
}
