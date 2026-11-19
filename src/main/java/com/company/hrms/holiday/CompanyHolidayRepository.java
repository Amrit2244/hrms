package com.company.hrms.holiday;

import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CompanyHolidayRepository extends JpaRepository<CompanyHoliday, UUID> {
    List<CompanyHoliday> findAllByOrganization_IdOrderByHolidayDateAsc(UUID organizationId);
    boolean existsByOrganization_IdAndHolidayDate(UUID organizationId, LocalDate date);
    Optional<CompanyHoliday> findByIdAndOrganization_Id(UUID id, UUID organizationId);
}
