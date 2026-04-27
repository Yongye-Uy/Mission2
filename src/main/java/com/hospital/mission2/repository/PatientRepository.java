package com.hospital.mission2.repository;

import com.hospital.mission2.model.Patient;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
    public interface PatientRepository extends MongoRepository<Patient, String>{

    }

