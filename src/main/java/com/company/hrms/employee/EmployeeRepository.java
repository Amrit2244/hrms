package com.company.hrms.employee;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;

public interface EmployeeRepository extends JpaRepository<Employee, UUID> {
    @EntityGraph(attributePaths = "department")
    Page<Employee> findAllByOrderByCreatedAtDesc(Pageable pageable);
    @EntityGraph(attributePaths = "department")
    Page<Employee> findAllByOrganization_IdOrderByCreatedAtDesc(UUID organizationId, Pageable pageable);
    Optional<Employee> findByIdAndOrganization_Id(UUID id, UUID organizationId);
    long countByOrganization_Id(UUID organizationId);
    boolean existsByEmployeeNumberIgnoreCase(String employeeNumber);
    boolean existsByWorkEmailIgnoreCase(String workEmail);
    Optional<Employee> findByOrganization_IdAndWorkEmailIgnoreCase(UUID organizationId, String workEmail);
}
