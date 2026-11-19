package com.company.hrms.registration;

import com.company.hrms.organization.Organization;
import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "registration_requests")
public class RegistrationRequest {
    @Id
    private UUID id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "organization_id", nullable = false)
    private Organization organization;

    @Column(nullable = false, length = 80)
    private String username;

    @Column(nullable = false, length = 160)
    private String email;

    @Column(name = "password_hash", nullable = false, length = 100)
    private String passwordHash;

    @Column(name = "first_name", nullable = false, length = 80)
    private String firstName;

    @Column(name = "last_name", nullable = false, length = 80)
    private String lastName;

    @Column(name = "phone_number", length = 30)
    private String phoneNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private RegistrationStatus status = RegistrationStatus.PENDING;

    @Column(name = "reviewed_by", length = 80)
    private String reviewedBy;

    @Column(name = "reviewed_at")
    private Instant reviewedAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    protected RegistrationRequest() {
    }

    public RegistrationRequest(UUID id, Organization organization, String username, String email,
                               String passwordHash, String firstName, String lastName, String phoneNumber) {
        this.id = id;
        this.organization = organization;
        this.username = username;
        this.email = email;
        this.passwordHash = passwordHash;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
        this.status = RegistrationStatus.PENDING;
    }

    public void approve(String reviewer) {
        this.status = RegistrationStatus.APPROVED;
        this.reviewedBy = reviewer;
        this.reviewedAt = Instant.now();
    }

    public void reject(String reviewer) {
        this.status = RegistrationStatus.REJECTED;
        this.reviewedBy = reviewer;
        this.reviewedAt = Instant.now();
    }

    public UUID getId() { return id; }
    public Organization getOrganization() { return organization; }
    public String getUsername() { return username; }
    public String getEmail() { return email; }
    public String getPasswordHash() { return passwordHash; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getPhoneNumber() { return phoneNumber; }
    public RegistrationStatus getStatus() { return status; }
    public Instant getCreatedAt() { return createdAt; }
}
