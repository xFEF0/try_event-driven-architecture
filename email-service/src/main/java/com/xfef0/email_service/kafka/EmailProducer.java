package com.xfef0.email_service.kafka;

import com.xfef0.base_domains.dto.OrderEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
@Slf4j
@Service
public class EmailProducer {

    @Value("${spring.activemq.queue.name}")
    private String destinationQueue;
    private final JmsTemplate jmsTemplate;

    @KafkaListener(
            topics = "${spring.kafka.topic.name}",
            groupId = "${spring.kafka.consumer.group-id}"
    )
    public void publishEmailMessage(OrderEvent event) {
        log.info("Order event received in email service -> {}", event);
        Map<String, String> kv = new HashMap<>();
        kv.put("name", event.getOrder().getName());
        kv.put("price", String.valueOf(event.getOrder().getPrice()));
        kv.put("quantity", String.valueOf(event.getOrder().getQuantity()));

        jmsTemplate.convertAndSend(destinationQueue, kv.toString());
        log.info("Email message sent");
    }
}
