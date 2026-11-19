package com.company.hrms.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.company.hrms.registration.RegistrationService;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/register")
public class RegistrationController {
    private final RegistrationService registrations;

    public RegistrationController(RegistrationService registrations) {
        this.registrations = registrations;
    }

    @GetMapping
    public String form(Model model) {
        model.addAttribute("form", new RegistrationService.RegistrationForm());
        return "auth/register";
    }

    @PostMapping
    public String submit(@Valid @ModelAttribute("form") RegistrationService.RegistrationForm form,
                         BindingResult result, Model model) {
        if (!form.getPassword().equals(form.getConfirmPassword())) {
            result.rejectValue("confirmPassword", "mismatch", "Passwords do not match.");
        }
        if (result.hasErrors()) return "auth/register";

        try {
            registrations.submit(form);
        } catch (RegistrationService.RegistrationConflictException ex) {
            model.addAttribute("registrationError", ex.getMessage());
            return "auth/register";
        }
        return "redirect:/login?registered";
    }
}
