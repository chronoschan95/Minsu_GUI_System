package com.minsu.model.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Order {
    private Long id;
    private Long houseId;
    private Long guestId;
    private String checkIn;
    private String checkOut;
    private String status;  // PENDING, CONFIRMED, CANCELLED, COMPLETED
    private Double totalPrice;
    private String createdAt;
}
