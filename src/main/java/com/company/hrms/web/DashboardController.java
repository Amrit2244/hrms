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

@Controller
public class DashboardController {
    private final EmployeeRepository employees;
    private final DepartmentRepository departments;
    private final AttendanceRepository attendance;
    private final LeaveRequestRepository leaves;
    private final OrganizationRepository organizations;

    public DashboardController(EmployeeRepository employees, DepartmentRepository departments,
            AttendanceRepository attendance, LeaveRequestRepository leaves, OrganizationRepository organizations) {
        this.employees = employees;
        this.departments = departments;
        this.attendance = attendance;
        this.leaves = leaves;
        this.organizations = organizations;
    }

    @GetMapping({"/", "/dashboard"})
    public String dashboard(Model model) {
        var organization = organizations.findAll().stream().findFirst().orElseThrow();
        var organizationId = organization.getId();
        var today = LocalDate.now();
        model.addAttribute("employeeCount", employees.count());
        model.addAttribute("departmentCount", departments.count());
        model.addAttribute("presentToday", attendance.countByOrganization_IdAndAttendanceDateAndStatus(organizationId, today, "PRESENT"));
        model.addAttribute("pendingLeaveCount", leaves.countByOrganization_IdAndStatus(organizationId, "PENDING"));
        return "dashboard/index";
    }
}
