package com.sitemasterpro.controller;

import com.sitemasterpro.dto.ProjectDto;
import com.sitemasterpro.entity.Project;
import com.sitemasterpro.entity.ProjectMilestone;
import com.sitemasterpro.service.ProjectService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@Controller
@RequestMapping("/api/projects")
public class ProjectController {

    @Autowired
    private ProjectService projectService;

    @GetMapping
    @ResponseBody
    public ResponseEntity<List<Project>> getAllProjects() {
        List<Project> projects = projectService.getAllProjects();
        return ResponseEntity.ok(projects);
    }

    @GetMapping("/{id}")
    @ResponseBody
    public ResponseEntity<Project> getProject(@PathVariable Long id) {
        Project project = projectService.getProjectById(id);
        return ResponseEntity.ok(project);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    @ResponseBody
    public ResponseEntity<Project> createProject(@Valid @RequestBody ProjectDto projectDto) {
        Project project = convertToEntity(projectDto);
        Project savedProject = projectService.createProject(project);
        return ResponseEntity.ok(savedProject);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN') or hasRole('SITE_MANAGER')")
    @ResponseBody
    public ResponseEntity<Project> updateProject(@PathVariable Long id, @Valid @RequestBody ProjectDto projectDto) {
        Project project = convertToEntity(projectDto);
        project.setId(id);
        Project updatedProject = projectService.updateProject(project);
        return ResponseEntity.ok(updatedProject);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @ResponseBody
    public ResponseEntity<?> deleteProject(@PathVariable Long id) {
        projectService.deleteProject(id);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}/progress")
    @PreAuthorize("hasRole('SITE_MANAGER') or hasRole('SITE_ENGINEER') or hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    @ResponseBody
    public ResponseEntity<?> updateProjectProgress(@PathVariable Long id, @RequestParam BigDecimal progress) {
        projectService.updateProjectProgress(id, progress);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}/milestones")
    @ResponseBody
    public ResponseEntity<List<ProjectMilestone>> getProjectMilestones(@PathVariable Long id) {
        List<ProjectMilestone> milestones = projectService.getProjectMilestones(id);
        return ResponseEntity.ok(milestones);
    }

    @PostMapping("/{id}/milestones")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN') or hasRole('SITE_MANAGER')")
    @ResponseBody
    public ResponseEntity<ProjectMilestone> createMilestone(@PathVariable Long id, @Valid @RequestBody ProjectMilestone milestone) {
        Project project = projectService.getProjectById(id);
        milestone.setProject(project);
        ProjectMilestone savedMilestone = projectService.createMilestone(milestone);
        return ResponseEntity.ok(savedMilestone);
    }

    @PutMapping("/milestones/{milestoneId}/status")
    @PreAuthorize("hasRole('SITE_MANAGER') or hasRole('SITE_ENGINEER') or hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    @ResponseBody
    public ResponseEntity<?> updateMilestoneStatus(@PathVariable Long milestoneId, 
                                                  @RequestParam ProjectMilestone.MilestoneStatus status) {
        projectService.updateMilestoneStatus(milestoneId, status);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/overdue")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN') or hasRole('CEO')")
    @ResponseBody
    public ResponseEntity<List<Project>> getOverdueProjects() {
        List<Project> overdueProjects = projectService.getOverdueProjects();
        return ResponseEntity.ok(overdueProjects);
    }

    @GetMapping("/over-budget")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN') or hasRole('CEO') or hasRole('ACCOUNTANT')")
    @ResponseBody
    public ResponseEntity<List<Project>> getProjectsOverBudget() {
        List<Project> overBudgetProjects = projectService.getProjectsOverBudget();
        return ResponseEntity.ok(overBudgetProjects);
    }

    // Thymeleaf views
    @GetMapping("/view")
    public String projectsView(Model model) {
        List<Project> projects = projectService.getAllProjects();
        model.addAttribute("projects", projects);
        return "projects";
    }

    @GetMapping("/{id}/view")
    public String projectDetailView(@PathVariable Long id, Model model) {
        Project project = projectService.getProjectById(id);
        List<ProjectMilestone> milestones = projectService.getProjectMilestones(id);
        model.addAttribute("project", project);
        model.addAttribute("milestones", milestones);
        return "project/detail";
    }

    private Project convertToEntity(ProjectDto dto) {
        Project project = new Project();
        project.setName(dto.getName());
        project.setDescription(dto.getDescription());
        project.setLocation(dto.getLocation());
        project.setStartDate(dto.getStartDate());
        project.setEndDate(dto.getEndDate());
        project.setContractValue(dto.getContractValue());
        project.setBudgetedCost(dto.getBudgetedCost());
        project.setStatus(dto.getStatus() != null ? dto.getStatus() : Project.ProjectStatus.PLANNING);
        return project;
    }
}
