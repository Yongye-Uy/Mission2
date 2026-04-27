package com.hospital.mission2.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;

@Document(collection = "appointments")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Appointment {
    @Id
    private String id;
    private String patientId;
    private String doctorId;
    private LocalDateTime appointmentDate;
    private String status;       
    private List<PrescribedItem> prescribedItems;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PrescribedItem {
        private String inventoryItemId;
        private int quantity;
    }
}