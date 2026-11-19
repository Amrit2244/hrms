
package com.company.hrms.registration;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.company.hrms.bootstrap.RoleLookup;
import com.company.hrms.bootstrap.UserAccountFactory;
import com.company.hrms.employee.Employee;
import com.company.hrms.employee.EmployeeRepository;
import com.company.hrms.organization.Organization;
import com.company.hrms.organization.OrganizationRepository;
import com.company.hrms.user.UserAccountRepository;

import jakarta.transaction.Transactional;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Service
public class RegistrationService {

    private final RegistrationRequestRepository requests;
    private final UserAccountRepository users;
    private final OrganizationRepository organizations;
    private final EmployeeRepository employees;
    private final RoleLookup roles;
    private final PasswordEncoder passwordEncoder;

    public RegistrationService(
            RegistrationRequestRepository requests,
            UserAccountRepository users,
            OrganizationRepository organizations,
            EmployeeRepository employees,
            RoleLookup roles,
            PasswordEncoder passwordEncoder) {

        this.requests = requests;
        this.users = users;
        this.organizations = organizations;
        this.employees = employees;
        this.roles = roles;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public void submit(RegistrationForm form) {

        String username = form.getUsername().trim();
        String email = form.getEmail().trim().toLowerCase();

        if (users.existsByUsernameIgnoreCase(username)
                || requests.existsByUsernameIgnoreCaseAndStatus(
                        username, RegistrationStatus.PENDING)) {

            throw new RegistrationConflictException(
                    "That username is already registered or awaiting approval.");
        }

        if (users.existsByEmailIgnoreCase(email)
                || requests.existsByEmailIgnoreCaseAndStatus(
                        email, RegistrationStatus.PENDING)) {

            throw new RegistrationConflictException(
                    "That email address is already registered or awaiting approval.");
        }

        if (!form.getPassword().equals(form.getConfirmPassword())) {
            throw new RegistrationConflictException(
                    "Password and confirm password do not match.");
        }

        if (form.getPassword().getBytes(StandardCharsets.UTF_8).length > 72) {
            throw new RegistrationConflictException(
                    "Password must be no more than 72 UTF-8 bytes.");
        }

        Organization organization = organizations.findAll()
                .stream()
                .findFirst()
                .orElseThrow(() -> new IllegalStateException(
                        "The company has not been initialized yet."));

        RegistrationRequest request = new RegistrationRequest(
                UUID.randomUUID(),
                organization,
                username,
                email,
                passwordEncoder.encode(form.getPassword()),
                form.getFirstName().trim(),
                form.getLastName().trim(),
                StringUtils.hasText(form.getPhoneNumber())
                        ? form.getPhoneNumber().trim()
                        : null
        );

        requests.save(request);
    }

    @Transactional
    public void approve(UUID requestId, String reviewer) {

        RegistrationRequest request = requests.findById(requestId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Registration request was not found."));

        ensurePending(request);

        if (users.existsByUsernameIgnoreCase(request.getUsername())) {
            throw new RegistrationConflictException(
                    "The username is already in use. Reject this request and ask the employee to register with another username.");
        }

        if (users.existsByEmailIgnoreCase(request.getEmail())) {
            throw new RegistrationConflictException(
                    "The email address is already in use by another account.");
        }

        // Reuse an employee record if HR has already created one.
        if (employees.findByOrganization_IdAndWorkEmailIgnoreCase(
                request.getOrganization().getId(),
                request.getEmail()).isEmpty()) {

            String employeeNumber = "SELF-"
                    + UUID.randomUUID()
                    .toString()
                    .substring(0, 12)
                    .toUpperCase();

            employees.save(new Employee(
                    UUID.randomUUID(),
                    request.getOrganization(),
                    null,
                    employeeNumber,
                    request.getFirstName(),
                    request.getLastName(),
                    request.getEmail(),
                    request.getPhoneNumber(),
                    null,
                    null
            ));
        }

        users.save(UserAccountFactory.create(
                request.getOrganization(),
                request.getUsername(),
                request.getEmail(),
                request.getPasswordHash(),
                roles.findEmployee()
        ));

        request.approve(reviewer);
    }

    @Transactional
    public void reject(UUID requestId, String reviewer) {

        RegistrationRequest request = requests.findById(requestId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Registration request was not found."));

        ensurePending(request);

        request.reject(reviewer);
    }

    private void ensurePending(RegistrationRequest request) {

        if (request.getStatus() != RegistrationStatus.PENDING) {
            throw new IllegalStateException(
                    "This registration request has already been reviewed.");
        }
    }

    public static class RegistrationForm {

        @NotBlank
        @Size(max = 80)
        private String firstName;

        @NotBlank
        @Size(max = 80)
        private String lastName;

        @NotBlank
        @Email
        @Size(max = 160)
        private String email;

        @Size(max = 30)
        private String phoneNumber;

        @NotBlank
        @Pattern(
                regexp = "[A-Za-z0-9._-]{4,40}",
                message = "Use 4–40 letters, numbers, dots, underscores or hyphens."
        )
        private String username;

        @NotBlank
        @Size(
                min = 12,
                max = 72,
                message = "Password must contain at least 12 characters and no more than 72 characters."
        )
        private String password;

        @NotBlank
        private String confirmPassword;

        // Getters and setters required by Thymeleaf

        public String getFirstName() {
            return firstName;
        }

        public void setFirstName(String firstName) {
            this.firstName = firstName;
        }

        public String getLastName() {
            return lastName;
        }

        public void setLastName(String lastName) {
            this.lastName = lastName;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getPhoneNumber() {
            return phoneNumber;
        }

        public void setPhoneNumber(String phoneNumber) {
            this.phoneNumber = phoneNumber;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public String getConfirmPassword() {
            return confirmPassword;
        }

        public void setConfirmPassword(String confirmPassword) {
            this.confirmPassword = confirmPassword;
        }
    }

    public static class RegistrationConflictException
            extends RuntimeException {

        public RegistrationConflictException(String message) {
            super(message);
        }
    }
}
