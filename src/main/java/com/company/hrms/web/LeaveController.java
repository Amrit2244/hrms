package com.company.hrms.web;

import com.company.hrms.employee.*; import com.company.hrms.leave.*; import com.company.hrms.organization.*;
import jakarta.validation.Valid; import jakarta.validation.constraints.*;
import org.springframework.stereotype.Controller; import org.springframework.ui.Model; import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*; import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.time.*; import java.util.UUID;

@Controller @RequestMapping("/leave-requests")
public class LeaveController {
 private final LeaveRequestRepository leaves; private final EmployeeRepository employees; private final OrganizationRepository organizations;
 public LeaveController(LeaveRequestRepository l,EmployeeRepository e,OrganizationRepository o){leaves=l;employees=e;organizations=o;}
 @GetMapping public String list(Model model){var org=organizations.findAll().stream().findFirst().orElseThrow();model.addAttribute("requests",leaves.findAllByOrganization_IdOrderByCreatedAtDesc(org.getId()));model.addAttribute("form",new LeaveForm());model.addAttribute("employees",employees.findAll());return "leave/list";}
 @PostMapping public String create(@Valid @ModelAttribute("form") LeaveForm form,BindingResult result,Model model,RedirectAttributes flash){
  var org=organizations.findAll().stream().findFirst().orElseThrow(); Employee employee=null;
  try{employee=employees.findById(UUID.fromString(form.getEmployeeId())).orElse(null);}catch(Exception ignored){}
  if(employee==null)result.rejectValue("employeeId","invalid","Select a valid employee.");
  if(form.getStartDate()!=null&&form.getEndDate()!=null&&form.getEndDate().isBefore(form.getStartDate()))result.rejectValue("endDate","invalid","End date must be on or after start date.");
  if(result.hasErrors()){model.addAttribute("requests",leaves.findAllByOrganization_IdOrderByCreatedAtDesc(org.getId()));model.addAttribute("employees",employees.findAll());return "leave/list";}
  leaves.save(new LeaveRequest(UUID.randomUUID(),org,employee,form.getLeaveType(),form.getStartDate(),form.getEndDate(),form.getReason().trim()));flash.addFlashAttribute("successMessage","Leave request submitted for review.");return "redirect:/leave-requests";
 }
 @PostMapping("/{id}/review") public String review(@PathVariable UUID id,@RequestParam String decision,@RequestParam(required=false) String comment,org.springframework.security.core.Authentication authentication,RedirectAttributes flash){
  LeaveRequest request=leaves.findById(id).orElseThrow();
  if(!"PENDING".equals(request.getStatus())){flash.addFlashAttribute("errorMessage","This request has already been reviewed.");return "redirect:/leave-requests";}
  if(!decision.equals("APPROVED")&&!decision.equals("REJECTED")){flash.addFlashAttribute("errorMessage","Invalid review decision.");return "redirect:/leave-requests";}
  request.review(decision,authentication.getName(),comment);leaves.save(request);flash.addFlashAttribute("successMessage","Leave request "+decision.toLowerCase()+".");return "redirect:/leave-requests";
 }
 public static class LeaveForm {
  @NotBlank private String employeeId; @NotBlank private String leaveType; @NotNull @FutureOrPresent private LocalDate startDate; @NotNull @FutureOrPresent private LocalDate endDate; @NotBlank @Size(max=1000) private String reason;
  public String getEmployeeId(){return employeeId;} public void setEmployeeId(String v){employeeId=v;} public String getLeaveType(){return leaveType;} public void setLeaveType(String v){leaveType=v;}
  public LocalDate getStartDate(){return startDate;} public void setStartDate(LocalDate v){startDate=v;} public LocalDate getEndDate(){return endDate;} public void setEndDate(LocalDate v){endDate=v;}
  public String getReason(){return reason;} public void setReason(String v){reason=v;}
 }
}
