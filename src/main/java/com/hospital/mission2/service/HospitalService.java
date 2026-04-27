package com.hospital.mission2.service;

import com.hospital.mission2.model.*;
import com.hospital.mission2.repository.*;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class HospitalService {

    private final PatientRepository patientRepo;
    private final DoctorRepository doctorRepo;
    private final InventoryRepository inventoryRepo;
    private final AppointmentRepository appointmentRepo;
    private final BillingRepository billingRepo;

    public HospitalService(PatientRepository patientRepo,
                           DoctorRepository doctorRepo,
                           InventoryRepository inventoryRepo,
                           AppointmentRepository appointmentRepo,
                           BillingRepository billingRepo) {
        this.patientRepo = patientRepo;
        this.doctorRepo = doctorRepo;
        this.inventoryRepo = inventoryRepo;
        this.appointmentRepo = appointmentRepo;
        this.billingRepo = billingRepo;
    }

    public List<Patient> allPatients() { return patientRepo.findAll(); }
    public Patient savePatient(Patient p) { return patientRepo.save(p); }

    public List<Doctor> allDoctors() { return doctorRepo.findAll(); }
    public Doctor saveDoctor(Doctor d) { return doctorRepo.save(d); }

    public List<Inventory> allInventory() { return inventoryRepo.findAll(); }
    public Inventory saveInventory(Inventory i) { return inventoryRepo.save(i); }

    public Appointment saveAppointment(Appointment a) { return appointmentRepo.save(a); }
    public List<Appointment> allAppointments() { return appointmentRepo.findAll(); }

    public Optional<Patient> getPatient(String id) { return patientRepo.findById(id); }
    public Optional<Doctor> getDoctor(String id) { return doctorRepo.findById(id); }
    public Optional<Inventory> getInventoryItem(String id) { return inventoryRepo.findById(id); }
    public Optional<Appointment> getAppointment(String id) { return appointmentRepo.findById(id); }

    public Billing saveBilling(Billing b) { return billingRepo.save(b); }
    public List<Billing> allBillings() { return billingRepo.findAll(); }
    public List<Billing> billingsByPatient(String patientId) { return billingRepo.findByPatientId(patientId); }
    public List<Billing> unpaidBillings() { return billingRepo.findByPaymentStatus("unpaid"); }
    public Optional<Billing> getBilling(String id) { return billingRepo.findById(id); }

    public List<Inventory> itemsNearExpiration() {
        return inventoryRepo.findByExpirationDateBefore(LocalDate.now().plusDays(30));
    }
}