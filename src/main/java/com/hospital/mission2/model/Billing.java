package com.hospital.mission2.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Document(collection = "billings")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Billing {
    @Id
    private String id;
    private String patientId;          
    private String appointmentId;
    private double amount;
    private LocalDateTime billingDate;
    private String paymentStatus;
}