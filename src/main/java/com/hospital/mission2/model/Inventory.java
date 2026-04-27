package com.hospital.mission2.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDate;

@Document(collection = "inventory")
@Data
@NoArgsConstructor
@AllArgsConstructor

public class Inventory {
    @Id
    private String id;
    private String itemName;
    private int quantity;
    private LocalDate expirationDate;
}
