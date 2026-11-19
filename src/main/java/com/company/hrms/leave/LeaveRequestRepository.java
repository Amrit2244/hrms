package com.company.hrms.leave;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;
public interface LeaveRequestRepository extends JpaRepository<LeaveRequest, UUID> {
    List<LeaveRequest> findAllByOrganization_IdOrderByCreatedAtDesc(UUID organizationId);
    long countByOrganization_IdAndStatus(UUID organizationId, String status);
}
