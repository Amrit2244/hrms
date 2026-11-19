package com.company.hrms.web;

import com.company.hrms.attendance.*;
import com.company.hrms.employee.*;
import com.company.hrms.organization.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.time.*;
import java.util.UUID;

@Controller @RequestMapping("/attendance")
public class AttendanceController {
 private final AttendanceRepository records; private final EmployeeRepository employees; private final OrganizationRepository organizations;
 public AttendanceController(AttendanceRepository r,EmployeeRepository e,OrganizationRepository o){records=r;employees=e;organizations=o;}
 @GetMapping public String list(@RequestParam(required=false) @DateTimeFormat(iso=DateTimeFormat.ISO.DATE) LocalDate date,Model model){
  LocalDate selected=date==null?LocalDate.now():date; var org=organizations.findAll().stream().findFirst().orElseThrow();
  model.addAttribute("selectedDate",selected); model.addAttribute("records",records.findAllByOrganization_IdAndAttendanceDateOrderByEmployee_FirstNameAsc(org.getId(),selected));
  model.addAttribute("form",new AttendanceForm()); model.addAttribute("employees",employees.findAllByOrderByCreatedAtDesc(PageRequest.of(0,500)).getContent());
  return "attendance/list";
 }
 @PostMapping public String save(@Valid @ModelAttribute("form") AttendanceForm form,BindingResult result,Model model,RedirectAttributes flash){
  var org=organizations.findAll().stream().findFirst().orElseThrow(); Employee employee=null;
  try { employee=employees.findById(UUID.fromString(form.getEmployeeId())).orElse(null); } catch(Exception ignored) {}
  if(employee==null) result.rejectValue("employeeId","invalid","Select a valid employee.");
  if(employee!=null && records.existsByEmployee_IdAndAttendanceDate(employee.getId(),form.getAttendanceDate())) result.rejectValue("employeeId","duplicate","Attendance already exists for this employee and date.");
  if(result.hasErrors()){model.addAttribute("selectedDate",form.getAttendanceDate()==null?LocalDate.now():form.getAttendanceDate());model.addAttribute("records",records.findAllByOrganization_IdAndAttendanceDateOrderByEmployee_FirstNameAsc(org.getId(),form.getAttendanceDate()==null?LocalDate.now():form.getAttendanceDate()));model.addAttribute("employees",employees.findAll());return "attendance/list";}
  records.save(new AttendanceRecord(UUID.randomUUID(),org,employee,form.getAttendanceDate(),form.getCheckIn(),form.getCheckOut(),form.getStatus(),form.getNotes()));
  flash.addFlashAttribute("successMessage","Attendance saved."); return "redirect:/attendance?date="+form.getAttendanceDate();
 }
 public static class AttendanceForm {
  @NotBlank private String employeeId; @NotNull @PastOrPresent private LocalDate attendanceDate=LocalDate.now();
  private LocalTime checkIn; private LocalTime checkOut; @NotBlank private String status="PRESENT"; @Size(max=500) private String notes;
  public String getEmployeeId(){return employeeId;} public void setEmployeeId(String v){employeeId=v;} public LocalDate getAttendanceDate(){return attendanceDate;} public void setAttendanceDate(LocalDate v){attendanceDate=v;}
  public LocalTime getCheckIn(){return checkIn;} public void setCheckIn(LocalTime v){checkIn=v;} public LocalTime getCheckOut(){return checkOut;} public void setCheckOut(LocalTime v){checkOut=v;}
  public String getStatus(){return status;} public void setStatus(String v){status=v;} public String getNotes(){return notes;} public void setNotes(String v){notes=v;}
 }
}
