package com.minsu.model.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class House {
    private Long id;
    private Long hostId;
    private String title;
    private String description;
    private Double price;
    private String status; // AVAILABLE, BOOKED, OFFLINE
    private String createdAt;
    private LocalDateTime publishTime;

    public LocalDateTime getPublishTime() {
        return publishTime;
    }

    public void setPublishTime(LocalDateTime publishTime) {
        this.publishTime = publishTime;
    }
}
