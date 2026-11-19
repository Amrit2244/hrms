package com.company.hrms.web;

import com.company.hrms.department.*;
import com.company.hrms.employee.*;
import com.company.hrms.organization.OrganizationRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.time.LocalDate;
import java.util.UUID;

@Controller @RequestMapping("/employees")
public class EmployeeController {
    private final EmployeeRepository employees; private final DepartmentRepository departments; private final OrganizationRepository organizations;
    public EmployeeController(EmployeeRepository e,DepartmentRepository d,OrganizationRepository o){employees=e;departments=d;organizations=o;}
    @GetMapping public String list(@RequestParam(defaultValue="0") int page,Model model){model.addAttribute("employees",employees.findAllByOrderByCreatedAtDesc(PageRequest.of(Math.max(page,0),20)));return "employees/list";}
    @GetMapping("/new") public String createForm(Model model){model.addAttribute("form",new EmployeeForm());model.addAttribute("departments",departments.findAllByOrderByNameAsc());return "employees/form";}
    @PostMapping public String create(@Valid @ModelAttribute("form") EmployeeForm form,BindingResult result,Model model,RedirectAttributes flash){
        if(employees.existsByEmployeeNumberIgnoreCase(form.employeeNumber)) result.rejectValue("employeeNumber","duplicate","Employee number already exists.");
        if(employees.existsByWorkEmailIgnoreCase(form.workEmail)) result.rejectValue("workEmail","duplicate","Work email already exists.");
        if(result.hasErrors()){model.addAttribute("departments",departments.findAllByOrderByNameAsc());return "employees/form";}
        var org=organizations.findAll().stream().findFirst().orElseThrow();
        Department dept=(form.departmentId==null||form.departmentId.isBlank())?null:departments.findById(UUID.fromString(form.departmentId)).orElse(null);
        employees.save(new Employee(UUID.randomUUID(),org,dept,form.employeeNumber.trim(),form.firstName.trim(),form.lastName.trim(),form.workEmail.trim().toLowerCase(),form.phoneNumber,form.jobTitle,form.joiningDate));
        flash.addFlashAttribute("successMessage","Employee created successfully.");return "redirect:/employees";
    }
    public static class EmployeeForm {
        @NotBlank @Size(max=30) public String employeeNumber;
        @NotBlank @Size(max=80) public String firstName;
        @NotBlank @Size(max=80) public String lastName;
        @NotBlank @Email @Size(max=160) public String workEmail;
        @Size(max=30) public String phoneNumber;
        @Size(max=120) public String jobTitle;
        public String departmentId;
        @PastOrPresent public LocalDate joiningDate;
    }
}
