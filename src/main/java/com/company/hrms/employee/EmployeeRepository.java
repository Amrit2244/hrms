package com.company.hrms.employee;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface EmployeeRepository extends JpaRepository<Employee, UUID> {
    Page<Employee> findAllByOrderByCreatedAtDesc(Pageable pageable);
    boolean existsByEmployeeNumberIgnoreCase(String employeeNumber);
    boolean existsByWorkEmailIgnoreCase(String workEmail);
    Optional<Employee> findByOrganization_IdAndWorkEmailIgnoreCase(UUID organizationId, String workEmail);
}
