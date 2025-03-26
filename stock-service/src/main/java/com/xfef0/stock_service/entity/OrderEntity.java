package com.xfef0.stock_service.entity;

import com.xfef0.base_domains.OrderStatus;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Entity
public class OrderEntity {

    @Id
    private String id;
    private String name;
    private int quantity;
    private double price;
    private OrderStatus status;
    private LocalDateTime lastModification;
}
