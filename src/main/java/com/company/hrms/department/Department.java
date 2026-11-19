package com.company.hrms.department;

import com.company.hrms.organization.Organization;
import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity @Table(name="departments", uniqueConstraints=@UniqueConstraint(name="uk_department_org_name",columnNames={"organization_id","name"}))
public class Department {
    @Id private UUID id;
    @ManyToOne(optional=false,fetch=FetchType.LAZY) @JoinColumn(name="organization_id") private Organization organization;
    @Column(nullable=false,length=100) private String name;
    @Column(length=500) private String description;
    @Column(nullable=false) private boolean active=true;
    @Column(name="created_at",nullable=false,updatable=false) private Instant createdAt=Instant.now();
    protected Department() {}
    public Department(UUID id,Organization organization,String name,String description){this.id=id;this.organization=organization;this.name=name;this.description=description;}
    public UUID getId(){return id;} public String getName(){return name;} public String getDescription(){return description;} public boolean isActive(){return active;}
}
