package com.company.hrms.holiday;
import com.company.hrms.organization.Organization;
import jakarta.persistence.*;
import java.time.*;
import java.util.UUID;
@Entity @Table(name="company_holidays", uniqueConstraints=@UniqueConstraint(name="uk_holiday_org_date",columnNames={"organization_id","holiday_date"}))
public class CompanyHoliday {
 @Id private UUID id;
 @ManyToOne(optional=false,fetch=FetchType.LAZY) @JoinColumn(name="organization_id") private Organization organization;
 @Column(name="holiday_date",nullable=false) private LocalDate holidayDate;
 @Column(nullable=false,length=120) private String name;
 @Column(length=500) private String description;
 @Column(name="created_at",nullable=false,updatable=false) private Instant createdAt=Instant.now();
 protected CompanyHoliday(){}
 public CompanyHoliday(UUID id,Organization org,LocalDate date,String name,String description){this.id=id;this.organization=org;this.holidayDate=date;this.name=name;this.description=description;}
 public UUID getId(){return id;} public LocalDate getHolidayDate(){return holidayDate;} public String getName(){return name;} public String getDescription(){return description;}
}
