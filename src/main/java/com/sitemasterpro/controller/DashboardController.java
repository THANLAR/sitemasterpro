package com.sitemasterpro.controller;

import com.sitemasterpro.dto.DashboardDto;
import com.sitemasterpro.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

@Controller
@RequestMapping("/dashboard")
public class DashboardController {

    @Autowired
    private ReportService reportService;

    @GetMapping
    public String dashboard(Model model) {
        return "dashboard";
    }

    @GetMapping("/api/data")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getDashboardData() {
        Map<String, Object> dashboardData = reportService.generateDashboardData();
        return ResponseEntity.ok(dashboardData);
    }

    @GetMapping("/super-admin")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public String superAdminDashboard(Model model) {
        Map<String, Object> dashboardData = reportService.generateDashboardData();
        model.addAllAttributes(dashboardData);
        return "dashboard/super-admin";
    }

    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public String adminDashboard(Model model) {
        Map<String, Object> dashboardData = reportService.generateDashboardData();
        model.addAllAttributes(dashboardData);
        return "dashboard/admin";
    }

    @GetMapping("/ceo")
    @PreAuthorize("hasRole('CEO') or hasRole('SUPER_ADMIN')")
    public String ceoDashboard(Model model) {
        Map<String, Object> dashboardData = reportService.generateDashboardData();
        model.addAllAttributes(dashboardData);
        return "dashboard/ceo";
    }

    @GetMapping("/accountant")
    @PreAuthorize("hasRole('ACCOUNTANT') or hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public String accountantDashboard(Model model) {
        Map<String, Object> dashboardData = reportService.generateDashboardData();
        model.addAllAttributes(dashboardData);
        return "dashboard/accountant";
    }

    @GetMapping("/store-keeper")
    @PreAuthorize("hasRole('STORE_KEEPER') or hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public String storeKeeperDashboard(Model model) {
        Map<String, Object> dashboardData = reportService.generateDashboardData();
        model.addAllAttributes(dashboardData);
        return "dashboard/store-keeper";
    }

    @GetMapping("/site-manager")
    @PreAuthorize("hasRole('SITE_MANAGER') or hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public String siteManagerDashboard(Model model) {
        Map<String, Object> dashboardData = reportService.generateDashboardData();
        model.addAllAttributes(dashboardData);
        return "dashboard/site-manager";
    }

    @GetMapping("/site-engineer")
    @PreAuthorize("hasRole('SITE_ENGINEER') or hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public String siteEngineerDashboard(Model model) {
        Map<String, Object> dashboardData = reportService.generateDashboardData();
        model.addAllAttributes(dashboardData);
        return "dashboard/site-engineer";
    }

    @GetMapping("/labor-head")
    @PreAuthorize("hasRole('LABOR_HEAD') or hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public String laborHeadDashboard(Model model) {
        Map<String, Object> dashboardData = reportService.generateDashboardData();
        model.addAllAttributes(dashboardData);
        return "dashboard/labor-head";
    }
}
