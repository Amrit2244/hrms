package com.company.hrms.department;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;
public interface DepartmentRepository extends JpaRepository<Department,UUID> {
    List<Department> findAllByOrderByNameAsc();
    boolean existsByOrganizationIdAndNameIgnoreCase(UUID organizationId,String name);
}
