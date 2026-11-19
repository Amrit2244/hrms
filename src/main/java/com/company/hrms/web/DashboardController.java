package com.company.hrms.web;
import com.company.hrms.employee.EmployeeRepository;
import com.company.hrms.department.DepartmentRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
@Controller
public class DashboardController {
    private final EmployeeRepository employees; private final DepartmentRepository departments;
    public DashboardController(EmployeeRepository employees,DepartmentRepository departments){this.employees=employees;this.departments=departments;}
    @GetMapping({"/","/dashboard"}) public String dashboard(Model model){model.addAttribute("employeeCount",employees.count());model.addAttribute("departmentCount",departments.count());return "dashboard/index";}
}
