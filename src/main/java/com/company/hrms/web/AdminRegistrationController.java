package com.company.hrms.web;

import com.company.hrms.registration.RegistrationRequestRepository;
import com.company.hrms.registration.RegistrationService;
import com.company.hrms.registration.RegistrationStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.UUID;

@Controller
@RequestMapping("/admin/registrations")
public class AdminRegistrationController {
    private final RegistrationRequestRepository requests;
    private final RegistrationService registrations;

    public AdminRegistrationController(RegistrationRequestRepository requests, RegistrationService registrations) {
        this.requests = requests;
        this.registrations = registrations;
    }

    @GetMapping
    public String pending(Model model) {
        model.addAttribute("requests", requests.findAllByStatusOrderByCreatedAtAsc(RegistrationStatus.PENDING));
        return "admin/registrations";
    }

    @PostMapping("/{id}/approve")
    public String approve(@PathVariable UUID id, Principal principal, RedirectAttributes flash) {
        try {
            registrations.approve(id, principal.getName());
            flash.addFlashAttribute("successMessage", "Registration approved. The employee can now sign in.");
        } catch (RuntimeException ex) {
            flash.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/admin/registrations";
    }

    @PostMapping("/{id}/reject")
    public String reject(@PathVariable UUID id, Principal principal, RedirectAttributes flash) {
        try {
            registrations.reject(id, principal.getName());
            flash.addFlashAttribute("successMessage", "Registration request rejected.");
        } catch (RuntimeException ex) {
            flash.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/admin/registrations";
    }
}
