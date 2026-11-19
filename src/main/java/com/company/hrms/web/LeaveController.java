package com.company.hrms.web;

import com.company.hrms.employee.Employee;
import com.company.hrms.employee.EmployeeRepository;
import com.company.hrms.leave.LeaveRequest;
import com.company.hrms.leave.LeaveRequestRepository;
import com.company.hrms.organization.OrganizationRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.security.core.Authentication;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/leave-requests")
public class LeaveController {
    private final LeaveRequestRepository leaves;
    private final EmployeeRepository employees;
    private final OrganizationRepository organizations;

    public LeaveController(LeaveRequestRepository leaves, EmployeeRepository employees,
                           OrganizationRepository organizations) {
        this.leaves = leaves;
        this.employees = employees;
        this.organizations = organizations;
    }

    @GetMapping
    public String list(Model model) {
        var organization = organizations.findAll().stream().findFirst()
                .orElseThrow(() -> new IllegalStateException("No organization is configured."));
        populateList(model, organization.getId());
        model.addAttribute("form", new LeaveForm());
        return "leave/list";
    }

    @PostMapping
    public String create(@Valid @ModelAttribute("form") LeaveForm form, BindingResult result,
                         Model model, RedirectAttributes flash) {
        var organization = organizations.findAll().stream().findFirst()
                .orElseThrow(() -> new IllegalStateException("No organization is configured."));
        Employee employee = null;
        if (StringUtils.hasText(form.getEmployeeId())) {
            try { employee = employees.findByIdAndOrganization_Id(UUID.fromString(form.getEmployeeId()), organization.getId()).orElse(null); }
            catch (IllegalArgumentException ignored) { /* report invalid employee below */ }
        }
        if (employee == null) result.rejectValue("employeeId", "invalid", "Select a valid employee.");
        if (form.getStartDate() != null && form.getEndDate() != null) {
            if (form.getEndDate().isBefore(form.getStartDate())) {
                result.rejectValue("endDate", "invalid", "End date must be on or after start date.");
            } else if (employee != null && leaves.existsByEmployee_IdAndStatusInAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
                    employee.getId(), List.of("PENDING", "APPROVED"), form.getEndDate(), form.getStartDate())) {
                result.rejectValue("startDate", "overlap", "This employee already has a pending or approved leave overlapping these dates.");
            }
        }
        if (result.hasErrors()) {
            populateList(model, organization.getId());
            return "leave/list";
        }
        leaves.save(new LeaveRequest(UUID.randomUUID(), organization, employee, form.getLeaveType(),
                form.getStartDate(), form.getEndDate(), form.getReason().trim()));
        flash.addFlashAttribute("successMessage", "Leave request submitted for review.");
        return "redirect:/leave-requests";
    }

    @PostMapping("/{id}/review")
    public String review(@PathVariable UUID id, @RequestParam String decision,
                         @RequestParam(required = false) String comment,
                         Authentication authentication, RedirectAttributes flash) {
        var organization = organizations.findAll().stream().findFirst()
                .orElseThrow(() -> new IllegalStateException("No organization is configured."));
        LeaveRequest request = leaves.findByIdAndOrganization_Id(id, organization.getId()).orElse(null);
        if (request == null) {
            flash.addFlashAttribute("errorMessage", "Leave request was not found.");
            return "redirect:/leave-requests";
        }
        if (!"PENDING".equals(request.getStatus())) {
            flash.addFlashAttribute("errorMessage", "This request has already been reviewed.");
            return "redirect:/leave-requests";
        }
        if (!"APPROVED".equals(decision) && !"REJECTED".equals(decision)) {
            flash.addFlashAttribute("errorMessage", "Invalid review decision.");
            return "redirect:/leave-requests";
        }
        if (comment != null && comment.length() > 500) {
            flash.addFlashAttribute("errorMessage", "Reviewer comment must be 500 characters or fewer.");
            return "redirect:/leave-requests";
        }
        request.review(decision, authentication.getName(), StringUtils.hasText(comment) ? comment.trim() : null);
        leaves.save(request);
        flash.addFlashAttribute("successMessage", "Leave request " + decision.toLowerCase() + ".");
        return "redirect:/leave-requests";
    }

    private void populateList(Model model, UUID organizationId) {
        model.addAttribute("requests", leaves.findAllForOrganizationWithEmployee(organizationId));
        model.addAttribute("employees", employees.findAllByOrganization_IdOrderByCreatedAtDesc(organizationId, org.springframework.data.domain.PageRequest.of(0, 500)).getContent());
    }

    public static class LeaveForm {
        @NotBlank private String employeeId;
        @NotBlank @jakarta.validation.constraints.Pattern(regexp = "CASUAL|SICK|EARNED|UNPAID|OTHER") private String leaveType;
        @NotNull @FutureOrPresent private LocalDate startDate;
        @NotNull @FutureOrPresent private LocalDate endDate;
        @NotBlank @Size(max = 1000) private String reason;
        public String getEmployeeId() { return employeeId; }
        public void setEmployeeId(String value) { employeeId = value; }
        public String getLeaveType() { return leaveType; }
        public void setLeaveType(String value) { leaveType = value; }
        public LocalDate getStartDate() { return startDate; }
        public void setStartDate(LocalDate value) { startDate = value; }
        public LocalDate getEndDate() { return endDate; }
        public void setEndDate(LocalDate value) { endDate = value; }
        public String getReason() { return reason; }
        public void setReason(String value) { reason = value; }
    }

}
