package com.xfef0.stock_service.kafka;

import com.xfef0.base_domains.dto.Order;
import com.xfef0.base_domains.dto.OrderEvent;
import com.xfef0.stock_service.entity.OrderEntity;
import com.xfef0.stock_service.exception.IllegalEventException;
import com.xfef0.stock_service.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@RequiredArgsConstructor
@Slf4j
@Service
public class OrderConsumer {

    private final OrderRepository orderRepository;

    @KafkaListener(
            topics = "${spring.kafka.topic.name}",
            groupId = "${spring.kafka.consumer.group-id}"
    )
    public void consume(OrderEvent event) {
        log.info("Order event received in stock service -> {}", event);
        OrderEntity orderEntity = getOrderEntity(event);
        orderRepository.save(orderEntity);
        log.info("Order event saved in stock service");
    }

    private static OrderEntity getOrderEntity(OrderEvent event) {
        Order order = event.getOrder();
        if (order == null) {
            throw new IllegalEventException("Null Order");
        }
        OrderEntity orderEntity = new OrderEntity();
        orderEntity.setId(order.getOrderId());
        orderEntity.setName(order.getName());
        orderEntity.setQuantity(order.getQuantity());
        orderEntity.setPrice(order.getPrice());
        orderEntity.setStatus(event.getStatus());
        orderEntity.setLastModification(LocalDateTime.now());
        return orderEntity;
    }
}
