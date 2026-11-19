
package com.company.hrms.web;

import java.util.UUID;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.company.hrms.department.Department;
import com.company.hrms.department.DepartmentRepository;
import com.company.hrms.organization.OrganizationRepository;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Controller
@RequestMapping("/departments")
public class DepartmentController {

    private final DepartmentRepository departments;
    private final OrganizationRepository organizations;

    public DepartmentController(
            DepartmentRepository departments,
            OrganizationRepository organizations) {

        this.departments = departments;
        this.organizations = organizations;
    }

    @GetMapping
    public String list(Model model) {

        model.addAttribute(
                "departments",
                departments.findAllByOrderByNameAsc()
        );

        model.addAttribute("form", new DepartmentForm());

        return "departments/list";
    }

    @PostMapping
    public String create(
            @Valid @ModelAttribute("form") DepartmentForm form,
            BindingResult result,
            Model model,
            RedirectAttributes flash) {

        String departmentName = form.getName() == null
                ? ""
                : form.getName().trim();

        if (departmentName.isEmpty()) {
            result.rejectValue(
                    "name",
                    "required",
                    "Department name is required."
            );
        } else {

            var org = organizations.findAll()
                    .stream()
                    .findFirst()
                    .orElseThrow();

            if (departments.existsByOrganizationIdAndNameIgnoreCase(
                    org.getId(), departmentName)) {

                result.rejectValue(
                        "name",
                        "duplicate",
                        "Department already exists."
                );
            }
        }

        if (result.hasErrors()) {

            model.addAttribute(
                    "departments",
                    departments.findAllByOrderByNameAsc()
            );

            return "departments/list";
        }

        var org = organizations.findAll()
                .stream()
                .findFirst()
                .orElseThrow();

        Department department = new Department(
                UUID.randomUUID(),
                org,
                departmentName,
                form.getDescription()
        );

        departments.save(department);

        flash.addFlashAttribute(
                "successMessage",
                "Department created successfully."
        );

        return "redirect:/departments";
    }

    public static class DepartmentForm {

        @NotBlank(message = "Department name is required.")
        @Size(max = 100, message = "Department name cannot exceed 100 characters.")
        private String name;

        @Size(max = 500, message = "Description cannot exceed 500 characters.")
        private String description;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }
    }
}
