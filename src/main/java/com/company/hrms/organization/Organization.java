package com.company.hrms.organization;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity @Table(name="organizations")
public class Organization {
    @Id private UUID id;
    @Column(nullable=false, length=160) private String name;
    @Column(name="created_at", nullable=false, updatable=false) private Instant createdAt=Instant.now();
    protected Organization() {}
    public Organization(UUID id,String name){this.id=id;this.name=name;}
    public UUID getId(){return id;} public String getName(){return name;}
}
