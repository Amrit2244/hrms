package com.company.hrms.attendance;

import com.company.hrms.employee.Employee;
import com.company.hrms.organization.Organization;
import jakarta.persistence.*;
import java.time.*;
import java.util.UUID;

@Entity
@Table(name = "attendance_records", uniqueConstraints = @UniqueConstraint(name = "uk_attendance_employee_date", columnNames = {"employee_id", "attendance_date"}))
public class AttendanceRecord {
    @Id private UUID id;
    @ManyToOne(optional = false, fetch = FetchType.LAZY) @JoinColumn(name = "organization_id") private Organization organization;
    @ManyToOne(optional = false, fetch = FetchType.LAZY) @JoinColumn(name = "employee_id") private Employee employee;
    @Column(name = "attendance_date", nullable = false) private LocalDate attendanceDate;
    @Column(name = "check_in") private LocalTime checkIn;
    @Column(name = "check_out") private LocalTime checkOut;
    @Column(nullable = false, length = 20) private String status = "PRESENT";
    @Column(length = 500) private String notes;
    @Column(name = "created_at", nullable = false, updatable = false) private Instant createdAt = Instant.now();
    @Column(name = "updated_at", nullable = false) private Instant updatedAt = Instant.now();
    protected AttendanceRecord() {}
    public AttendanceRecord(UUID id, Organization organization, Employee employee, LocalDate date, LocalTime in, LocalTime out, String status, String notes) {
        this.id=id; this.organization=organization; this.employee=employee; this.attendanceDate=date; this.checkIn=in; this.checkOut=out; this.status=status; this.notes=notes;
    }
    @PreUpdate void updateTimestamp() { updatedAt = Instant.now(); }
    public UUID getId(){return id;} public Employee getEmployee(){return employee;} public LocalDate getAttendanceDate(){return attendanceDate;}
    public LocalTime getCheckIn(){return checkIn;} public LocalTime getCheckOut(){return checkOut;} public String getStatus(){return status;} public String getNotes(){return notes;}
}
