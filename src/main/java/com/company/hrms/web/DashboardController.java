package com.company.hrms.web;

import com.company.hrms.attendance.AttendanceRepository;
import com.company.hrms.department.DepartmentRepository;
import com.company.hrms.employee.EmployeeRepository;
import com.company.hrms.leave.LeaveRequestRepository;
import com.company.hrms.organization.OrganizationRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.LocalDate;
import java.util.List;

@Controller
public class DashboardController {
    private final EmployeeRepository employees;
    private final DepartmentRepository departments;
    private final AttendanceRepository attendance;
    private final LeaveRequestRepository leaves;
    private final OrganizationRepository organizations;

    public DashboardController(EmployeeRepository employees, DepartmentRepository departments,
                               AttendanceRepository attendance, LeaveRequestRepository leaves,
                               OrganizationRepository organizations) {
        this.employees = employees;
        this.departments = departments;
        this.attendance = attendance;
        this.leaves = leaves;
        this.organizations = organizations;
    }

    @GetMapping({"/", "/dashboard"})
    public String dashboard(Model model) {
        var organization = organizations.findAll().stream().findFirst()
                .orElseThrow(() -> new IllegalStateException("No organization is configured."));
        LocalDate today = LocalDate.now();
        var organizationId = organization.getId();
        model.addAttribute("employeeCount", employees.countByOrganization_Id(organizationId));
        model.addAttribute("departmentCount", departments.countByOrganization_Id(organizationId));
        model.addAttribute("presentToday", attendance.countByOrganization_IdAndAttendanceDateAndStatusIn(
                organizationId, today, List.of("PRESENT", "HALF_DAY", "WORK_FROM_HOME")));
        model.addAttribute("absentToday", attendance.countByOrganization_IdAndAttendanceDateAndStatus(
                organizationId, today, "ABSENT"));
        model.addAttribute("pendingLeaveCount", leaves.countByOrganization_IdAndStatus(organizationId, "PENDING"));
        model.addAttribute("dashboardDate", today);
        return "dashboard/index";
    }
}
