package com.hospital.mission2.repository;

import com.hospital.mission2.model.Billing;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface BillingRepository extends MongoRepository<Billing, String> {
    List<Billing> findByPatientId(String patientId);
    List<Billing> findByPaymentStatus(String paymentStatus);
}