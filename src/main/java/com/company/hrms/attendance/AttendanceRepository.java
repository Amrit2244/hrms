package com.company.hrms.attendance;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.*;
public interface AttendanceRepository extends JpaRepository<AttendanceRecord, UUID> {
    List<AttendanceRecord> findAllByOrganization_IdAndAttendanceDateOrderByEmployee_FirstNameAsc(UUID organizationId, LocalDate date);
    boolean existsByEmployee_IdAndAttendanceDate(UUID employeeId, LocalDate date);
    long countByOrganization_IdAndAttendanceDateAndStatus(UUID organizationId, LocalDate date, String status);
}
