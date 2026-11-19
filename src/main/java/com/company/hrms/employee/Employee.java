package com.company.hrms.employee;

import com.company.hrms.department.Department;
import com.company.hrms.organization.Organization;
import jakarta.persistence.*;
import java.time.*;
import java.util.UUID;

@Entity @Table(name="employees", uniqueConstraints={@UniqueConstraint(name="uk_employee_org_number",columnNames={"organization_id","employee_number"}),@UniqueConstraint(name="uk_employee_org_email",columnNames={"organization_id","work_email"})})
public class Employee {
    @Id private UUID id;
    @ManyToOne(optional=false,fetch=FetchType.LAZY) @JoinColumn(name="organization_id") private Organization organization;
    @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="department_id") private Department department;
    @Column(name="employee_number",nullable=false,length=30) private String employeeNumber;
    @Column(name="first_name",nullable=false,length=80) private String firstName;
    @Column(name="last_name",nullable=false,length=80) private String lastName;
    @Column(name="work_email",nullable=false,length=160) private String workEmail;
    @Column(name="phone_number",length=30) private String phoneNumber;
    @Column(name="job_title",length=120) private String jobTitle;
    @Column(name="employment_status",nullable=false,length=20) private String employmentStatus="ACTIVE";
    @Column(name="joining_date") private LocalDate joiningDate;
    @Column(name="created_at",nullable=false,updatable=false) private Instant createdAt=Instant.now();
    @Column(name="updated_at",nullable=false) private Instant updatedAt=Instant.now();
    protected Employee() {}
    public Employee(UUID id,Organization org,Department department,String number,String first,String last,String email,String phone,String title,LocalDate joiningDate){
        this.id=id;this.organization=org;this.department=department;this.employeeNumber=number;this.firstName=first;this.lastName=last;this.workEmail=email;this.phoneNumber=phone;this.jobTitle=title;this.joiningDate=joiningDate;
    }
    @PreUpdate void onUpdate(){updatedAt=Instant.now();}
    public UUID getId(){return id;} public String getEmployeeNumber(){return employeeNumber;} public String getFirstName(){return firstName;} public String getLastName(){return lastName;}
    public String getWorkEmail(){return workEmail;} public String getPhoneNumber(){return phoneNumber;} public String getJobTitle(){return jobTitle;} public String getEmploymentStatus(){return employmentStatus;}
    public LocalDate getJoiningDate(){return joiningDate;} public Department getDepartment(){return department;}
}
