package com.company.hrms.bootstrap;

import com.company.hrms.user.Role;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Component;

@Component
public class RoleLookup {
    private final EntityManager entityManager;

    public RoleLookup(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public Role findSuperAdmin() {
        return findByName("SUPER_ADMIN");
    }

    public Role findEmployee() {
        return findByName("EMPLOYEE");
    }

    private Role findByName(String name) {
        return entityManager.createQuery("select r from Role r where r.name = :name", Role.class)
                .setParameter("name", name)
                .getSingleResult();
    }
}
