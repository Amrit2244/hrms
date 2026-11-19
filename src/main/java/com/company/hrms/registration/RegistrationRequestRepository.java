package com.company.hrms.registration;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface RegistrationRequestRepository extends JpaRepository<RegistrationRequest, UUID> {
    List<RegistrationRequest> findAllByStatusOrderByCreatedAtAsc(RegistrationStatus status);
    boolean existsByUsernameIgnoreCaseAndStatus(String username, RegistrationStatus status);
    boolean existsByEmailIgnoreCaseAndStatus(String email, RegistrationStatus status);
}
