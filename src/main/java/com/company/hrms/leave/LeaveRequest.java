package com.company.hrms.leave;

import com.company.hrms.employee.Employee;
import com.company.hrms.organization.Organization;
import jakarta.persistence.*;
import java.time.*;
import java.util.UUID;

@Entity @Table(name="leave_requests")
public class LeaveRequest {
    @Id private UUID id;
    @ManyToOne(optional=false, fetch=FetchType.LAZY) @JoinColumn(name="organization_id") private Organization organization;
    @ManyToOne(optional=false, fetch=FetchType.LAZY) @JoinColumn(name="employee_id") private Employee employee;
    @Column(name="leave_type", nullable=false, length=40) private String leaveType;
    @Column(name="start_date", nullable=false) private LocalDate startDate;
    @Column(name="end_date", nullable=false) private LocalDate endDate;
    @Column(nullable=false, length=1000) private String reason;
    @Column(nullable=false, length=20) private String status="PENDING";
    @Column(name="reviewed_by", length=80) private String reviewedBy;
    @Column(name="reviewed_at") private Instant reviewedAt;
    @Column(name="reviewer_comment", length=500) private String reviewerComment;
    @Column(name="created_at", nullable=false, updatable=false) private Instant createdAt=Instant.now();
    protected LeaveRequest() {}
    public LeaveRequest(UUID id, Organization org, Employee employee, String type, LocalDate start, LocalDate end, String reason) {
        this.id=id; this.organization=org; this.employee=employee; this.leaveType=type; this.startDate=start; this.endDate=end; this.reason=reason;
    }
    public void review(String status, String reviewer, String comment) { this.status=status; this.reviewedBy=reviewer; this.reviewedAt=Instant.now(); this.reviewerComment=comment; }
    public UUID getId(){return id;} public Employee getEmployee(){return employee;} public String getLeaveType(){return leaveType;} public LocalDate getStartDate(){return startDate;} public LocalDate getEndDate(){return endDate;} public String getReason(){return reason;} public String getStatus(){return status;} public String getReviewerComment(){return reviewerComment;}
}
