package com.hospital.mission2.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDate;

@Document(collection = "patients")
@Data
@NoArgsConstructor
@AllArgsConstructor


public class Patient {
    @Id
    private String id;
    private String firstName;
    private String lastName;
    private LocalDate dob;
    private String gender;
    private String address;
    private String phone;
    private String insuranceInfo;
}
