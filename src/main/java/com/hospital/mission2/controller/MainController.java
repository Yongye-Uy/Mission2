package com.hospital.mission2.controller;

import com.hospital.mission2.model.*;
import com.hospital.mission2.service.HospitalService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Controller
public class MainController {

    private final HospitalService service;

    public MainController(HospitalService service) {
        this.service = service;
    }

    @GetMapping("/")
    public String home() {
        return "index";
    }

    // =========== PATIENTS ===========
    @GetMapping("/patients")
    public String listPatients(Model model) {
        model.addAttribute("patients", service.allPatients());
        return "patient-list";
    }

    @GetMapping("/patients/new")
    public String newPatientForm(Model model) {
        model.addAttribute("patient", new Patient());
        return "patient-form";
    }

    @PostMapping("/patients")
    public String savePatient(@ModelAttribute Patient patient) {
        service.savePatient(patient);
        return "redirect:/patients";
    }

    @GetMapping("/patients/{id}")
    public String viewPatient(@PathVariable String id, Model model) {
        Optional<Patient> opt = service.getPatient(id);
        if (opt.isEmpty()) {
            return "redirect:/patients";   // if not found, go back to list
        }
        model.addAttribute("patient", opt.get());
        return "patient-detail";
    }

    // =========== DOCTORS ===========
    @GetMapping("/doctors")
    public String listDoctors(Model model) {
        model.addAttribute("doctors", service.allDoctors());
        return "doctor-list";
    }

    @GetMapping("/doctors/new")
    public String newDoctorForm(Model model) {
        model.addAttribute("doctor", new Doctor());
        return "doctor-form";
    }

    @PostMapping("/doctors")
    public String saveDoctor(@ModelAttribute Doctor doctor) {
        service.saveDoctor(doctor);
        return "redirect:/doctors";
    }

    // =========== INVENTORY (medicines) ===========
    @GetMapping("/inventory")
    public String listInventory(Model model) {
        model.addAttribute("items", service.allInventory());
        return "inventory-list";
    }

    @GetMapping("/inventory/new")
    public String newInventoryForm(Model model) {
        model.addAttribute("item", new Inventory());
        return "inventory-form";
    }

    @PostMapping("/inventory")
    public String saveInventory(@ModelAttribute Inventory item) {
        service.saveInventory(item);
        return "redirect:/inventory";
    }

    // =========== APPOINTMENT ===========
    @GetMapping("/appointments/new")
    public String newAppointmentForm(Model model) {
        model.addAttribute("appointment", new Appointment());
        model.addAttribute("patients", service.allPatients());
        model.addAttribute("doctors", service.allDoctors());
        model.addAttribute("inventory", service.allInventory());
        return "appointment-form";
    }

    @PostMapping("/appointments")
    public String createAppointment(@ModelAttribute Appointment appointment,
                                    @RequestParam(value = "invIds", required = false) List<String> invIds,
                                    @RequestParam(value = "quantities", required = false) List<Integer> quantities) {
        List<Appointment.PrescribedItem> items = new ArrayList<>();
        if (invIds != null && quantities != null) {
            for (int i = 0; i < invIds.size(); i++) {
                if (invIds.get(i) != null && !invIds.get(i).isEmpty()) {
                    String invId = invIds.get(i);
                    int qty = quantities.get(i);
                    Optional<Inventory> invOpt = service.getInventoryItem(invId);
                    if (invOpt.isPresent()) {
                        Inventory inv = invOpt.get();
                        if (inv.getQuantity() < qty) {
                            continue;
                        }
                        inv.setQuantity(inv.getQuantity() - qty);
                        service.saveInventory(inv);

                        // Build prescribed item
                        Appointment.PrescribedItem pi = new Appointment.PrescribedItem();
                        pi.setInventoryItemId(invId);
                        pi.setQuantity(qty);
                        items.add(pi);
                    }
                }
            }
        }
        appointment.setPrescribedItems(items);
        service.saveAppointment(appointment);
        return "redirect:/appointments";
    }

    @GetMapping("/appointments")
    public String listAppointments(Model model) {
        List<Appointment> apps = service.allAppointments();
        List<Map<String, Object>> enriched = new ArrayList<>();
        for (Appointment app : apps) {
            Map<String, Object> m = new HashMap<>();
            m.put("appointment", app);
            service.getPatient(app.getPatientId()).ifPresent(p ->
                    m.put("patientName", p.getFirstName() + " " + p.getLastName()));
            service.getDoctor(app.getDoctorId()).ifPresent(d ->
                    m.put("doctorName", d.getFirstName() + " " + d.getLastName()));

            List<String> medNames = new ArrayList<>();
            if (app.getPrescribedItems() != null) {
                for (Appointment.PrescribedItem pi : app.getPrescribedItems()) {
                    service.getInventoryItem(pi.getInventoryItemId())
                            .ifPresent(inv -> medNames.add(inv.getItemName()));
                }
            }
            m.put("medicines", medNames);
            enriched.add(m);
        }
        model.addAttribute("appointments", enriched);
        return "appointment-list";
    }

    @GetMapping("/appointments/{id}")
    public String viewReceipt(@PathVariable String id, Model model) {
        Optional<Appointment> opt = service.getAppointment(id);
        if (opt.isEmpty()) return "redirect:/appointments";
        Appointment app = opt.get();
        model.addAttribute("appointment", app);
        model.addAttribute("patient", service.getPatient(app.getPatientId()).orElse(null));
        model.addAttribute("doctor", service.getDoctor(app.getDoctorId()).orElse(null));
        Map<String, String> medicineNames = new HashMap<>();
        if (app.getPrescribedItems() != null) {
            for (Appointment.PrescribedItem pi : app.getPrescribedItems()) {
                service.getInventoryItem(pi.getInventoryItemId())
                        .ifPresent(inv -> medicineNames.put(pi.getInventoryItemId(), inv.getItemName()));
            }
        }
        model.addAttribute("medicineNames", medicineNames);
        return "receipt-view";
    }

    // =========== BILLINGS ===========
    @GetMapping("/billings/new")
    public String newBillingForm(Model model) {
        model.addAttribute("billing", new Billing());
        model.addAttribute("patients", service.allPatients());
        model.addAttribute("appointments", service.allAppointments());
        return "billing-form";
    }

    @PostMapping("/billings")
    public String saveBilling(@ModelAttribute Billing billing) {
        if (billing.getBillingDate() == null) {
            billing.setBillingDate(LocalDateTime.now());
        }
        service.saveBilling(billing);
        return "redirect:/billings";
    }

    @GetMapping("/billings")
    public String listBillings(Model model) {
        List<Billing> billings = service.allBillings();
        List<Map<String, Object>> enriched = new ArrayList<>();
        for (Billing bill : billings) {
            Map<String, Object> map = new HashMap<>();
            map.put("billing", bill);
            service.getPatient(bill.getPatientId()).ifPresent(p ->
                    map.put("patientName", p.getFirstName() + " " + p.getLastName()));
            enriched.add(map);
        }
        model.addAttribute("billings", enriched);
        return "billing-list";
    }

    // =========== REPORTS ===========
    @GetMapping("/reports/appointments-by-patient")
    public String appointmentsByPatient(@RequestParam(required = false) String patientId, Model model) {
        model.addAttribute("patients", service.allPatients());
        if (patientId != null && !patientId.isEmpty()) {
            List<Appointment> apps = service.allAppointments().stream()
                    .filter(a -> patientId.equals(a.getPatientId()))
                    .collect(Collectors.toList());
            model.addAttribute("appointments", apps);
        }
        return "report-appointments-by-patient";
    }

    @GetMapping("/reports/appointments-by-doctor")
    public String appointmentsByDoctor(@RequestParam(required = false) String doctorId, Model model) {
        model.addAttribute("doctors", service.allDoctors());
        if (doctorId != null && !doctorId.isEmpty()) {
            List<Appointment> apps = service.allAppointments().stream()
                    .filter(a -> doctorId.equals(a.getDoctorId()))
                    .collect(Collectors.toList());
            model.addAttribute("appointments", apps);
        }
        return "report-appointments-by-doctor";
    }

    @GetMapping("/reports/billing-by-patient")
    public String billingByPatient(@RequestParam(required = false) String patientId, Model model) {
        model.addAttribute("patients", service.allPatients());
        if (patientId != null && !patientId.isEmpty()) {
            List<Billing> bills = service.billingsByPatient(patientId);
            model.addAttribute("billings", bills);
            service.getPatient(patientId).ifPresent(p -> model.addAttribute("patient", p));
        }
        return "report-billing-by-patient";
    }

    @GetMapping("/reports/inventory-near-expiration")
    public String inventoryNearExpiration(Model model) {
        model.addAttribute("items", service.itemsNearExpiration());
        return "report-inventory-expiration";
    }

    @GetMapping("/reports/unpaid-billings")
    public String unpaidBillings(Model model) {
        List<Billing> unpaid = service.unpaidBillings();
        List<Map<String, Object>> enriched = new ArrayList<>();
        for (Billing b : unpaid) {
            Map<String, Object> m = new HashMap<>();
            m.put("billing", b);
            service.getPatient(b.getPatientId()).ifPresent(p ->
                    m.put("patientName", p.getFirstName() + " " + p.getLastName()));
            enriched.add(m);
        }
        model.addAttribute("unpaid", enriched);
        return "report-unpaid-billings";
    }

    @PostMapping("/billings/{id}/pay")
    public String markBillingAsPaid(@PathVariable String id) {
        Optional<Billing> opt = service.getBilling(id);
        if (opt.isPresent()) {
            Billing b = opt.get();
            b.setPaymentStatus("paid");
            service.saveBilling(b);
        }
        return "redirect:/reports/unpaid-billings";
    }
}