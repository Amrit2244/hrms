package com.company.hrms.holiday;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;
public interface CompanyHolidayRepository extends JpaRepository<CompanyHoliday,UUID> {
 List<CompanyHoliday> findAllByOrganization_IdOrderByHolidayDateAsc(UUID organizationId);
 boolean existsByOrganization_IdAndHolidayDate(UUID organizationId, java.time.LocalDate date);
}
