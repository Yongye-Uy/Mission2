package com.hospital.mission2.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Document(collection = "doctors")
@Data
@NoArgsConstructor
@AllArgsConstructor

public class Doctor {
    @Id
    private String id;
    private String firstName;
    private String lastName;
    private String specialization;
    private String schedule;

}
