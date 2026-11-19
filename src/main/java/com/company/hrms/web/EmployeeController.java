
package com.company.hrms.web;

import java.time.LocalDate;
import java.util.UUID;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.company.hrms.department.Department;
import com.company.hrms.department.DepartmentRepository;
import com.company.hrms.employee.Employee;
import com.company.hrms.employee.EmployeeRepository;
import com.company.hrms.organization.OrganizationRepository;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;

@Controller
@RequestMapping("/employees")
public class EmployeeController {

    private final EmployeeRepository employees;
    private final DepartmentRepository departments;
    private final OrganizationRepository organizations;

    public EmployeeController(
            EmployeeRepository employees,
            DepartmentRepository departments,
            OrganizationRepository organizations) {
        this.employees = employees;
        this.departments = departments;
        this.organizations = organizations;
    }

    @GetMapping
    public String list(
            @RequestParam(defaultValue = "0") int page,
            Model model) {

        model.addAttribute(
                "employees",
                employees.findAllByOrderByCreatedAtDesc(
                        PageRequest.of(Math.max(page, 0), 20)
                )
        );

        return "employees/list";
    }

    @GetMapping("/new")
    public String createForm(Model model) {

        model.addAttribute("form", new EmployeeForm());
        loadDepartments(model);

        return "employees/form";
    }

    @PostMapping
    public String create(
            @Valid @ModelAttribute("form") EmployeeForm form,
            BindingResult result,
            Model model,
            RedirectAttributes flash) {

        // Check duplicate employee number.
        if (StringUtils.hasText(form.getEmployeeNumber())
                && employees.existsByEmployeeNumberIgnoreCase(
                        form.getEmployeeNumber().trim())) {

            result.rejectValue(
                    "employeeNumber",
                    "duplicate",
                    "Employee number already exists."
            );
        }

        // Check duplicate work email.
        if (StringUtils.hasText(form.getWorkEmail())
                && employees.existsByWorkEmailIgnoreCase(
                        form.getWorkEmail().trim())) {

            result.rejectValue(
                    "workEmail",
                    "duplicate",
                    "Work email already exists."
            );
        }

        // Resolve department.
        Department department = null;

        if (StringUtils.hasText(form.getDepartmentId())) {
            try {
                UUID departmentId = UUID.fromString(
                        form.getDepartmentId()
                );

                department = departments.findById(departmentId)
                        .orElse(null);

                if (department == null) {
                    result.rejectValue(
                            "departmentId",
                            "invalid",
                            "Please select a valid department."
                    );
                }

            } catch (IllegalArgumentException ex) {
                result.rejectValue(
                        "departmentId",
                        "invalid",
                        "Invalid department selection."
                );
            }
        }

        // Return the form with validation errors.
        if (result.hasErrors()) {
            loadDepartments(model);
            return "employees/form";
        }

        // Find the organization.
        var organization = organizations.findAll()
                .stream()
                .findFirst()
                .orElseThrow(() ->
                        new IllegalStateException(
                                "No organization has been configured."
                        )
                );

        // Create employee.
        Employee employee = new Employee(
                UUID.randomUUID(),
                organization,
                department,
                form.getEmployeeNumber().trim(),
                form.getFirstName().trim(),
                form.getLastName().trim(),
                form.getWorkEmail().trim().toLowerCase(),
                form.getPhoneNumber(),
                form.getJobTitle(),
                form.getJoiningDate()
        );

        employees.save(employee);

        flash.addFlashAttribute(
                "successMessage",
                "Employee created successfully."
        );

        return "redirect:/employees";
    }

    private void loadDepartments(Model model) {
        model.addAttribute(
                "departments",
                departments.findAllByOrderByNameAsc()
        );
    }

    public static class EmployeeForm {

        @NotBlank
        @Size(max = 30)
        private String employeeNumber;

        @NotBlank
        @Size(max = 80)
        private String firstName;

        @NotBlank
        @Size(max = 80)
        private String lastName;

        @NotBlank
        @Email
        @Size(max = 160)
        private String workEmail;

        @Size(max = 30)
        private String phoneNumber;

        @Size(max = 120)
        private String jobTitle;

        private String departmentId;

        @PastOrPresent
        private LocalDate joiningDate;

        public String getEmployeeNumber() {
            return employeeNumber;
        }

        public void setEmployeeNumber(String employeeNumber) {
            this.employeeNumber = employeeNumber;
        }

        public String getFirstName() {
            return firstName;
        }

        public void setFirstName(String firstName) {
            this.firstName = firstName;
        }

        public String getLastName() {
            return lastName;
        }

        public void setLastName(String lastName) {
            this.lastName = lastName;
        }

        public String getWorkEmail() {
            return workEmail;
        }

        public void setWorkEmail(String workEmail) {
            this.workEmail = workEmail;
        }

        public String getPhoneNumber() {
            return phoneNumber;
        }

        public void setPhoneNumber(String phoneNumber) {
            this.phoneNumber = phoneNumber;
        }

        public String getJobTitle() {
            return jobTitle;
        }

        public void setJobTitle(String jobTitle) {
            this.jobTitle = jobTitle;
        }

        public String getDepartmentId() {
            return departmentId;
        }

        public void setDepartmentId(String departmentId) {
            this.departmentId = departmentId;
        }

        public LocalDate getJoiningDate() {
            return joiningDate;
        }

        public void setJoiningDate(LocalDate joiningDate) {
            this.joiningDate = joiningDate;
        }
    }
}
