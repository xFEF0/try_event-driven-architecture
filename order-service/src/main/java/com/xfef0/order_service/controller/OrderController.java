package com.xfef0.order_service.controller;

import com.xfef0.base_domains.OrderStatus;
import com.xfef0.base_domains.dto.Order;
import com.xfef0.base_domains.dto.OrderEvent;
import com.xfef0.order_service.kafka.OrderProducer;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/orders")
public class OrderController {

    private final OrderProducer orderProducer;

    @PostMapping
    public ResponseEntity<String> placeOrder(@RequestBody Order order) {
        order.setOrderId(UUID.randomUUID().toString());
        OrderEvent orderEvent = new OrderEvent();
        orderEvent.setStatus(OrderStatus.PENDING);
        orderEvent.setMessage("order status in pending state");
        orderEvent.setOrder(order);

        orderProducer.sendMessage(orderEvent);

        return new ResponseEntity<>( "Order placed", HttpStatus.CREATED);
    }
}
