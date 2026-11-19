package com.company.hrms.web;

import com.company.hrms.attendance.AttendanceRecord;
import com.company.hrms.attendance.AttendanceRepository;
import com.company.hrms.employee.Employee;
import com.company.hrms.employee.EmployeeRepository;
import com.company.hrms.organization.OrganizationRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.springframework.data.domain.PageRequest;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

@Controller
@RequestMapping("/attendance")
public class AttendanceController {
    private final AttendanceRepository records;
    private final EmployeeRepository employees;
    private final OrganizationRepository organizations;

    public AttendanceController(AttendanceRepository records, EmployeeRepository employees,
                                 OrganizationRepository organizations) {
        this.records = records;
        this.employees = employees;
        this.organizations = organizations;
    }

    @GetMapping
    public String list(@RequestParam(required = false)
                       @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
                       Model model) {
        LocalDate selected = date == null ? LocalDate.now() : date;
        var organization = organizations.findAll().stream().findFirst()
                .orElseThrow(() -> new IllegalStateException("No organization is configured."));
        model.addAttribute("selectedDate", selected);
        model.addAttribute("records", records.findAllByOrganizationAndDateWithEmployee(
                organization.getId(), selected));
        model.addAttribute("form", new AttendanceForm());
        model.addAttribute("employees", employees.findAllByOrganization_IdOrderByCreatedAtDesc(organization.getId(), PageRequest.of(0, 500)).getContent());
        return "attendance/list";
    }

    @PostMapping
    public String save(@Valid @ModelAttribute("form") AttendanceForm form,
                       BindingResult result, Model model, RedirectAttributes flash) {
        var organization = organizations.findAll().stream().findFirst()
                .orElseThrow(() -> new IllegalStateException("No organization is configured."));
        Employee employee = null;
        if (form.getEmployeeId() != null && !form.getEmployeeId().isBlank()) {
            try { employee = employees.findByIdAndOrganization_Id(UUID.fromString(form.getEmployeeId()), organization.getId()).orElse(null); }
            catch (IllegalArgumentException ignored) { /* validation below reports invalid selection */ }
        }
        if (employee == null) result.rejectValue("employeeId", "invalid", "Select a valid employee.");
        if (form.getCheckIn() != null && form.getCheckOut() != null
                && form.getCheckOut().isBefore(form.getCheckIn())) {
            result.rejectValue("checkOut", "invalid", "Check-out cannot be earlier than check-in.");
        }
        if (employee != null && form.getAttendanceDate() != null
                && records.existsByEmployee_IdAndAttendanceDate(employee.getId(), form.getAttendanceDate())) {
            result.rejectValue("employeeId", "duplicate", "Attendance already exists for this employee and date.");
        }
        if (result.hasErrors()) {
            LocalDate selected = form.getAttendanceDate() == null ? LocalDate.now() : form.getAttendanceDate();
            model.addAttribute("selectedDate", selected);
            model.addAttribute("records", records.findAllByOrganizationAndDateWithEmployee(
                    organization.getId(), selected));
            model.addAttribute("employees", employees.findAllByOrganization_IdOrderByCreatedAtDesc(organization.getId(), PageRequest.of(0, 500)).getContent());
            return "attendance/list";
        }
        records.save(new AttendanceRecord(UUID.randomUUID(), organization, employee,
                form.getAttendanceDate(), form.getCheckIn(), form.getCheckOut(), form.getStatus(), form.getNotes()));
        flash.addFlashAttribute("successMessage", "Attendance saved successfully.");
        return "redirect:/attendance?date=" + form.getAttendanceDate();
    }

    public static class AttendanceForm {
        @NotBlank private String employeeId;
        @NotNull @PastOrPresent private LocalDate attendanceDate = LocalDate.now();
        private LocalTime checkIn;
        private LocalTime checkOut;
        @NotBlank @Pattern(regexp = "PRESENT|ABSENT|HALF_DAY|WORK_FROM_HOME|ON_LEAVE")
        private String status = "PRESENT";
        @Size(max = 500) private String notes;
        public String getEmployeeId() { return employeeId; }
        public void setEmployeeId(String value) { employeeId = value; }
        public LocalDate getAttendanceDate() { return attendanceDate; }
        public void setAttendanceDate(LocalDate value) { attendanceDate = value; }
        public LocalTime getCheckIn() { return checkIn; }
        public void setCheckIn(LocalTime value) { checkIn = value; }
        public LocalTime getCheckOut() { return checkOut; }
        public void setCheckOut(LocalTime value) { checkOut = value; }
        public String getStatus() { return status; }
        public void setStatus(String value) { status = value; }
        public String getNotes() { return notes; }
        public void setNotes(String value) { notes = value; }
    }
}
