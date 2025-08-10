package com.sitemasterpro.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "labor_records")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class LaborRecord {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "worker_name", nullable = false)
    private String workerName;
    
    @Column(name = "worker_id")
    private String workerId;
    
    @Column(name = "job_title", nullable = false)
    private String jobTitle;
    
    @Column(name = "work_date", nullable = false)
    private LocalDate workDate;
    
    @Column(name = "hours_worked", precision = 5, scale = 2, nullable = false)
    private BigDecimal hoursWorked;
    
    @Column(name = "overtime_hours", precision = 5, scale = 2)
    private BigDecimal overtimeHours = BigDecimal.ZERO;
    
    @Column(name = "hourly_rate", precision = 10, scale = 2, nullable = false)
    private BigDecimal hourlyRate;
    
    @Column(name = "overtime_rate", precision = 10, scale = 2)
    private BigDecimal overtimeRate;
    
    @Column(name = "total_pay", precision = 10, scale = 2)
    private BigDecimal totalPay;
    
    @Column(name = "work_description", length = 500)
    private String workDescription;
    
    @Column(name = "attendance_status", nullable = false)
    private String attendanceStatus;
    
    @Column(length = 500)
    private String notes;
    
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;
    
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "recorded_by", nullable = false)
    private User recordedBy;
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    @PrePersist
    @PreUpdate
    protected void calculateTotalPay() {
        BigDecimal regularPay = hoursWorked.multiply(hourlyRate);
        BigDecimal overTimePay = BigDecimal.ZERO;
        
        if (overtimeHours != null && overtimeRate != null) {
            overTimePay = overtimeHours.multiply(overtimeRate);
        }
        
        totalPay = regularPay.add(overTimePay);
    }
}
