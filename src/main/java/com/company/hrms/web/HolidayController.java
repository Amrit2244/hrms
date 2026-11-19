package com.company.hrms.web;
import com.company.hrms.holiday.*; import com.company.hrms.organization.*; import jakarta.validation.Valid; import jakarta.validation.constraints.*;
import org.springframework.format.annotation.DateTimeFormat; import org.springframework.stereotype.Controller; import org.springframework.ui.Model; import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*; import org.springframework.web.servlet.mvc.support.RedirectAttributes; import java.time.*; import java.util.UUID;
@Controller @RequestMapping("/holidays")
public class HolidayController {
 private final CompanyHolidayRepository holidays; private final OrganizationRepository organizations;
 public HolidayController(CompanyHolidayRepository h,OrganizationRepository o){holidays=h;organizations=o;}
 @GetMapping public String list(Model model){var org=organizations.findAll().stream().findFirst().orElseThrow();model.addAttribute("holidays",holidays.findAllByOrganization_IdOrderByHolidayDateAsc(org.getId()));model.addAttribute("form",new HolidayForm());return "holidays/list";}
 @PostMapping public String create(@Valid @ModelAttribute("form") HolidayForm form,BindingResult result,Model model,RedirectAttributes flash){var org=organizations.findAll().stream().findFirst().orElseThrow();if(form.getHolidayDate()!=null&&holidays.existsByOrganization_IdAndHolidayDate(org.getId(),form.getHolidayDate()))result.rejectValue("holidayDate","duplicate","A holiday already exists on this date.");if(result.hasErrors()){model.addAttribute("holidays",holidays.findAllByOrganization_IdOrderByHolidayDateAsc(org.getId()));return "holidays/list";}holidays.save(new CompanyHoliday(UUID.randomUUID(),org,form.getHolidayDate(),form.getName().trim(),form.getDescription()));flash.addFlashAttribute("successMessage","Company holiday added.");return "redirect:/holidays";}
 @PostMapping("/{id}/delete") public String delete(@PathVariable UUID id,RedirectAttributes flash){holidays.deleteById(id);flash.addFlashAttribute("successMessage","Holiday deleted.");return "redirect:/holidays";}
 public static class HolidayForm {@NotNull @DateTimeFormat(iso=DateTimeFormat.ISO.DATE) private LocalDate holidayDate; @NotBlank @Size(max=120) private String name; @Size(max=500) private String description; public LocalDate getHolidayDate(){return holidayDate;}public void setHolidayDate(LocalDate v){holidayDate=v;}public String getName(){return name;}public void setName(String v){name=v;}public String getDescription(){return description;}public void setDescription(String v){description=v;}}
}
