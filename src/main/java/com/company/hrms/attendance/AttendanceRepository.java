package com.company.hrms.attendance;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface AttendanceRepository extends JpaRepository<AttendanceRecord, UUID> {
    @Query("select a from AttendanceRecord a join fetch a.employee where a.organization.id = :organizationId and a.attendanceDate = :date order by a.employee.firstName asc")
    List<AttendanceRecord> findAllByOrganizationAndDateWithEmployee(@Param("organizationId") UUID organizationId,
                                                                     @Param("date") LocalDate date);
    boolean existsByEmployee_IdAndAttendanceDate(UUID employeeId, LocalDate date);
    long countByOrganization_IdAndAttendanceDateAndStatus(UUID organizationId, LocalDate date, String status);
    long countByOrganization_IdAndAttendanceDateAndStatusIn(UUID organizationId, LocalDate date, List<String> statuses);
}
