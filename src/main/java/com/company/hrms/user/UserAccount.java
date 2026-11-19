package com.company.hrms.user;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

import com.company.hrms.organization.Organization;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity @Table(name="users")
public class UserAccount {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @ManyToOne(optional=false, fetch=FetchType.LAZY) @JoinColumn(name="organization_id") private Organization organization;
    @Column(nullable=false, unique=true, length=80) private String username;
    @Column(nullable=false, unique=true, length=160) private String email;
    @Column(name="password_hash", nullable=false, length=100) private String passwordHash;
    @Column(nullable=false) private boolean active=true;
    @Column(name="created_at", nullable=false, updatable=false) private Instant createdAt=Instant.now();
    @ManyToMany(fetch=FetchType.EAGER) @JoinTable(name="user_roles", joinColumns=@JoinColumn(name="user_id"), inverseJoinColumns=@JoinColumn(name="role_id"))
    private Set<Role> roles=new HashSet<>();
    protected UserAccount() {}
    public UserAccount(Organization organization, String username, String email, String passwordHash, Role role) {
        this.organization = organization; this.username = username; this.email = email; this.passwordHash = passwordHash; this.roles.add(role);
    }
    public Long getId(){return id;} public String getUsername(){return username;} public String getPasswordHash(){return passwordHash;}
    public boolean isActive(){return active;} public Set<Role> getRoles(){return roles;}
    public void resetPassword(String encodedPassword) {
    this.passwordHash = encodedPassword;
    this.active = true;
}
}
