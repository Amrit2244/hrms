package com.company.hrms.web;

import com.company.hrms.holiday.CompanyHoliday;
import com.company.hrms.holiday.CompanyHolidayRepository;
import com.company.hrms.organization.OrganizationRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.UUID;

@Controller
@RequestMapping("/holidays")
public class HolidayController {
    private final CompanyHolidayRepository holidays;
    private final OrganizationRepository organizations;

    public HolidayController(CompanyHolidayRepository holidays, OrganizationRepository organizations) {
        this.holidays = holidays;
        this.organizations = organizations;
    }

    @GetMapping
    public String list(Model model) {
        var organization = organizations.findAll().stream().findFirst()
                .orElseThrow(() -> new IllegalStateException("No organization is configured."));
        model.addAttribute("holidays", holidays.findAllByOrganization_IdOrderByHolidayDateAsc(organization.getId()));
        model.addAttribute("form", new HolidayForm());
        return "holidays/list";
    }

    @PostMapping
    public String create(@Valid @ModelAttribute("form") HolidayForm form, BindingResult result,
                         Model model, RedirectAttributes flash) {
        var organization = organizations.findAll().stream().findFirst()
                .orElseThrow(() -> new IllegalStateException("No organization is configured."));
        if (form.getHolidayDate() != null && holidays.existsByOrganization_IdAndHolidayDate(
                organization.getId(), form.getHolidayDate())) {
            result.rejectValue("holidayDate", "duplicate", "A holiday already exists on this date.");
        }
        if (result.hasErrors()) {
            model.addAttribute("holidays", holidays.findAllByOrganization_IdOrderByHolidayDateAsc(organization.getId()));
            return "holidays/list";
        }
        holidays.save(new CompanyHoliday(UUID.randomUUID(), organization, form.getHolidayDate(),
                form.getName().trim(), form.getDescription()));
        flash.addFlashAttribute("successMessage", "Company holiday added.");
        return "redirect:/holidays";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable UUID id, RedirectAttributes flash) {
        var organization = organizations.findAll().stream().findFirst()
                .orElseThrow(() -> new IllegalStateException("No organization is configured."));
        var holiday = holidays.findByIdAndOrganization_Id(id, organization.getId()).orElse(null);
        if (holiday == null) {
            flash.addFlashAttribute("errorMessage", "Holiday was not found.");
        } else {
            holidays.delete(holiday);
            flash.addFlashAttribute("successMessage", "Holiday deleted.");
        }
        return "redirect:/holidays";
    }

    public static class HolidayForm {
        @NotNull @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) private LocalDate holidayDate;
        @NotBlank @Size(max = 120) private String name;
        @Size(max = 500) private String description;
        public LocalDate getHolidayDate() { return holidayDate; }
        public void setHolidayDate(LocalDate value) { holidayDate = value; }
        public String getName() { return name; }
        public void setName(String value) { name = value; }
        public String getDescription() { return description; }
        public void setDescription(String value) { description = value; }
    }
}
