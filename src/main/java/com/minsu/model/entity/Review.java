package com.minsu.model.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Review {
    private Long id;
    private Long orderId;
    private Long houseId;
    private Long guestId;
    private Integer rating;
    private String comment;
    private String createdAt;
} 