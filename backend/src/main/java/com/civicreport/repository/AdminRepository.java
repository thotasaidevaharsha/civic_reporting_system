package com.civicreport.repository;

import com.civicreport.model.Admin;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AdminRepository extends JpaRepository<Admin, Long> {
    Optional<Admin> findByZoneIgnoreCaseAndAdminIdIgnoreCase(String zone, String adminId);
}
