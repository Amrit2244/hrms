
package com.company.hrms.bootstrap;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.StringUtils;

import com.company.hrms.organization.Organization;
import com.company.hrms.organization.OrganizationRepository;
import com.company.hrms.user.Role;
import com.company.hrms.user.UserAccount;
import com.company.hrms.user.UserAccountRepository;

@Configuration
public class InitialDataConfig {

    @Bean
    ApplicationRunner initialize(
            OrganizationRepository organizations,
            UserAccountRepository users,
            PasswordEncoder encoder,
            RoleLookup roleLookup,

            @Value("${COMPANY_NAME:Your Company}")
            String companyName,

            @Value("${ADMIN_USERNAME:}")
            String adminUsername,

            @Value("${ADMIN_PASSWORD:}")
            String adminPassword,

            @Value("${ADMIN_EMAIL:}")
            String adminEmail,

            @Value("${ADMIN_RESET_PASSWORD:false}")
            boolean resetExistingAdmin
    ) {
        return args -> {

            if (!StringUtils.hasText(adminUsername)
                    || !StringUtils.hasText(adminPassword)
                    || !StringUtils.hasText(adminEmail)) {
                throw new IllegalStateException(
                    "Set ADMIN_USERNAME, ADMIN_PASSWORD and ADMIN_EMAIL."
                );
            }

           if (adminPassword.length() < 4) {
           throw new IllegalStateException("ADMIN_PASSWORD must be at least 4 characters.");
            }

            Organization org = organizations.findAll()
                    .stream()
                    .findFirst()
                    .orElseGet(() -> organizations.save(
                            new Organization(UUID.randomUUID(), companyName)
                    ));

            var existingAdmin =
                    users.findByUsernameIgnoreCase(adminUsername);

            if (existingAdmin.isPresent()) {

                if (resetExistingAdmin) {
                    UserAccount account = existingAdmin.get();

                    account.resetPassword(
                            encoder.encode(adminPassword)
                    );

                    users.save(account);

                    System.out.println(
                        "ADMIN PASSWORD RESET SUCCESSFULLY."
                    );
                } else {
                    System.out.println(
                        "Existing admin found. Password unchanged."
                    );
                }

            } else {

                Role role = roleLookup.findSuperAdmin();

                UserAccount account = UserAccountFactory.create(
                        org,
                        adminUsername,
                        adminEmail,
                        encoder.encode(adminPassword),
                        role
                );

                users.save(account);

                System.out.println(
                    "INITIAL SUPER ADMIN CREATED SUCCESSFULLY."
                );
            }
        };
    }
}
