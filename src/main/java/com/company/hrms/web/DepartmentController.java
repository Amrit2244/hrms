package com.company.hrms.web;
import com.company.hrms.department.*;import com.company.hrms.organization.OrganizationRepository;
import jakarta.validation.Valid;import jakarta.validation.constraints.NotBlank;import jakarta.validation.constraints.Size;
import org.springframework.stereotype.Controller;import org.springframework.ui.Model;import org.springframework.validation.BindingResult;import org.springframework.web.bind.annotation.*;import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.util.UUID;
@Controller @RequestMapping("/departments")
public class DepartmentController {
 private final DepartmentRepository departments;private final OrganizationRepository organizations;
 public DepartmentController(DepartmentRepository d,OrganizationRepository o){departments=d;organizations=o;}
 @GetMapping public String list(Model model){model.addAttribute("departments",departments.findAllByOrderByNameAsc());model.addAttribute("form",new DepartmentForm());return "departments/list";}
 @PostMapping public String create(@Valid @ModelAttribute("form") DepartmentForm form,BindingResult result,Model model,RedirectAttributes flash){
  var org=organizations.findAll().stream().findFirst().orElseThrow();
  if(departments.existsByOrganizationIdAndNameIgnoreCase(org.getId(),form.name==null?"":form.name.trim()))result.rejectValue("name","duplicate","Department already exists.");
  if(result.hasErrors()){model.addAttribute("departments",departments.findAllByOrderByNameAsc());return "departments/list";}
  departments.save(new Department(UUID.randomUUID(),org,form.name.trim(),form.description));flash.addFlashAttribute("successMessage","Department created.");return "redirect:/departments";
 }
 public static class DepartmentForm{@NotBlank @Size(max=100) public String name;@Size(max=500) public String description;}
}
