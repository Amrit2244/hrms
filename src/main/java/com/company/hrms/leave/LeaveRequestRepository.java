package com.company.hrms.leave;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface LeaveRequestRepository extends JpaRepository<LeaveRequest, UUID> {
    @Query("select l from LeaveRequest l join fetch l.employee where l.organization.id = :organizationId order by l.createdAt desc")
    List<LeaveRequest> findAllForOrganizationWithEmployee(@Param("organizationId") UUID organizationId);
    long countByOrganization_IdAndStatus(UUID organizationId, String status);
    Optional<LeaveRequest> findByIdAndOrganization_Id(UUID id, UUID organizationId);
    boolean existsByEmployee_IdAndStatusInAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
            UUID employeeId, Collection<String> statuses, LocalDate requestedEnd, LocalDate requestedStart);
}
