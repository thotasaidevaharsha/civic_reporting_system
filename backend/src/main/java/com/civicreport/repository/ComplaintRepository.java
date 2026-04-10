package com.civicreport.repository;

import com.civicreport.model.Complaint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;

public interface ComplaintRepository extends JpaRepository<Complaint, Long>, JpaSpecificationExecutor<Complaint> {
    Optional<Complaint> findByTrackingIdIgnoreCase(String trackingId);

    List<Complaint> findByZoneIgnoreCase(String zone);
}
