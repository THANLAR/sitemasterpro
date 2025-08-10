package com.sitemasterpro.service;

import com.sitemasterpro.entity.Project;
import com.sitemasterpro.entity.ProjectMilestone;
import com.sitemasterpro.exception.CustomException;
import com.sitemasterpro.repository.ProjectRepository;
import com.sitemasterpro.repository.ProjectMilestoneRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
@Transactional
public class ProjectService {
    private static final Logger logger = LoggerFactory.getLogger(ProjectService.class);

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private ProjectMilestoneRepository milestoneRepository;

    @Autowired
    private AuditService auditService;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    public Project createProject(Project project) {
        Project savedProject = projectRepository.save(project);
        logger.info("Project created: {}", savedProject.getName());
        
        auditService.logAction("CREATE_PROJECT", "Project", savedProject.getId(), 
                              null, "Project created: " + savedProject.getName());
        
        // Notify via WebSocket
        messagingTemplate.convertAndSend("/topic/projects", 
                "New project created: " + savedProject.getName());
        
        return savedProject;
    }

    public Project updateProject(Project project) {
        Project existingProject = getProjectById(project.getId());
        String oldValues = String.format("name: %s, status: %s, completionPercentage: %s", 
                                        existingProject.getName(), existingProject.getStatus(), 
                                        existingProject.getCompletionPercentage());

        Project updatedProject = projectRepository.save(project);
        
        String newValues = String.format("name: %s, status: %s, completionPercentage: %s", 
                                        updatedProject.getName(), updatedProject.getStatus(), 
                                        updatedProject.getCompletionPercentage());
        
        auditService.logAction("UPDATE_PROJECT", "Project", project.getId(), oldValues, newValues);
        
        // Notify via WebSocket
        messagingTemplate.convertAndSend("/topic/projects", 
                "Project updated: " + updatedProject.getName());
        
        return updatedProject;
    }

    public Project getProjectById(Long id) {
        return projectRepository.findById(id)
                .orElseThrow(() -> new CustomException("Project not found with id: " + id));
    }

    public List<Project> getAllProjects() {
        return projectRepository.findAll();
    }

    public List<Project> getProjectsByStatus(Project.ProjectStatus status) {
        return projectRepository.findByStatus(status);
    }

    public List<Project> getOverdueProjects() {
        return projectRepository.findOverdueProjects(LocalDate.now());
    }

    public List<Project> getProjectsOverBudget() {
        return projectRepository.findProjectsOverBudget();
    }

    public void updateProjectFinancials(Long projectId, BigDecimal actualCost, BigDecimal actualRevenue) {
        Project project = getProjectById(projectId);
        
        BigDecimal oldCost = project.getActualCost();
        BigDecimal oldRevenue = project.getActualRevenue();
        
        project.setActualCost(actualCost);
        project.setActualRevenue(actualRevenue);
        
        projectRepository.save(project);
        
        auditService.logAction("UPDATE_PROJECT_FINANCIALS", "Project", projectId, 
                              String.format("cost: %s, revenue: %s", oldCost, oldRevenue),
                              String.format("cost: %s, revenue: %s", actualCost, actualRevenue));
        
        // Notify via WebSocket about financial changes
        messagingTemplate.convertAndSend("/topic/financial-updates", 
                String.format("Project %s financials updated - Cost: %s, Revenue: %s", 
                            project.getName(), actualCost, actualRevenue));
    }

    public void updateProjectProgress(Long projectId, BigDecimal completionPercentage) {
        Project project = getProjectById(projectId);
        BigDecimal oldPercentage = project.getCompletionPercentage();
        
        project.setCompletionPercentage(completionPercentage);
        
        // Auto-update status based on completion
        if (completionPercentage.compareTo(BigDecimal.ZERO) > 0 && 
            completionPercentage.compareTo(BigDecimal.valueOf(100)) < 0) {
            project.setStatus(Project.ProjectStatus.IN_PROGRESS);
        } else if (completionPercentage.compareTo(BigDecimal.valueOf(100)) >= 0) {
            project.setStatus(Project.ProjectStatus.COMPLETED);
            project.setActualEndDate(LocalDate.now());
        }
        
        projectRepository.save(project);
        
        auditService.logAction("UPDATE_PROJECT_PROGRESS", "Project", projectId, 
                              oldPercentage.toString(), completionPercentage.toString());
        
        // Notify via WebSocket
        messagingTemplate.convertAndSend("/topic/progress-updates", 
                String.format("Project %s progress updated to %s%%", 
                            project.getName(), completionPercentage));
    }

    public ProjectMilestone createMilestone(ProjectMilestone milestone) {
        ProjectMilestone savedMilestone = milestoneRepository.save(milestone);
        
        auditService.logAction("CREATE_MILESTONE", "ProjectMilestone", savedMilestone.getId(), 
                              null, "Milestone created: " + savedMilestone.getName());
        
        logger.info("Milestone created: {} for project: {}", 
                   savedMilestone.getName(), savedMilestone.getProject().getName());
        
        return savedMilestone;
    }

    public List<ProjectMilestone> getProjectMilestones(Long projectId) {
        return milestoneRepository.findByProjectId(projectId);
    }

    public List<ProjectMilestone> getOverdueMilestones() {
        return milestoneRepository.findOverdueMilestones(LocalDate.now());
    }

    public void updateMilestoneStatus(Long milestoneId, ProjectMilestone.MilestoneStatus status) {
        ProjectMilestone milestone = milestoneRepository.findById(milestoneId)
                .orElseThrow(() -> new CustomException("Milestone not found with id: " + milestoneId));
        
        ProjectMilestone.MilestoneStatus oldStatus = milestone.getStatus();
        milestone.setStatus(status);
        
        if (status == ProjectMilestone.MilestoneStatus.IN_PROGRESS && milestone.getActualStartDate() == null) {
            milestone.setActualStartDate(LocalDate.now());
        } else if (status == ProjectMilestone.MilestoneStatus.COMPLETED && milestone.getActualEndDate() == null) {
            milestone.setActualEndDate(LocalDate.now());
            milestone.setCompletionPercentage(BigDecimal.valueOf(100));
        }
        
        milestoneRepository.save(milestone);
        
        auditService.logAction("UPDATE_MILESTONE_STATUS", "ProjectMilestone", milestoneId, 
                              oldStatus.toString(), status.toString());
        
        // Notify via WebSocket
        messagingTemplate.convertAndSend("/topic/milestone-updates", 
                String.format("Milestone %s status changed to %s", milestone.getName(), status));
    }

    public BigDecimal calculateOverallProjectHealth() {
        List<Project> allProjects = getAllProjects();
        if (allProjects.isEmpty()) {
            return BigDecimal.ZERO;
        }

        long healthyProjects = allProjects.stream()
                .mapToLong(project -> {
                    boolean onTime = project.getEndDate() == null || 
                                   !LocalDate.now().isAfter(project.getEndDate());
                    boolean onBudget = project.getActualCost().compareTo(project.getBudgetedCost()) <= 0;
                    return (onTime && onBudget) ? 1 : 0;
                })
                .sum();

        return BigDecimal.valueOf(healthyProjects)
                .divide(BigDecimal.valueOf(allProjects.size()), 2, BigDecimal.ROUND_HALF_UP)
                .multiply(BigDecimal.valueOf(100));
    }

    public void deleteProject(Long projectId) {
        Project project = getProjectById(projectId);
        projectRepository.delete(project);
        
        auditService.logAction("DELETE_PROJECT", "Project", projectId, 
                              "Project: " + project.getName(), null);
        
        logger.info("Project deleted: {}", project.getName());
    }
}
