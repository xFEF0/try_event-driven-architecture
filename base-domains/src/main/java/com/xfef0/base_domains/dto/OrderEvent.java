package com.xfef0.base_domains.dto;

import com.xfef0.base_domains.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderEvent {
    private String message;
    private OrderStatus status;
    private Order order;
}
