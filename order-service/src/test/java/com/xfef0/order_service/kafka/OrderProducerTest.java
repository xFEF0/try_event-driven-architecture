package com.xfef0.order_service.kafka;

import com.xfef0.base_domains.OrderStatus;
import com.xfef0.base_domains.dto.Order;
import com.xfef0.base_domains.dto.OrderEvent;
import org.apache.kafka.clients.admin.NewTopic;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.messaging.Message;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@SpringBootTest
@DirtiesContext
@EmbeddedKafka(partitions = 1, brokerProperties = {"listeners=PLAINTEXT://localhost:9092", "port=9092"})
class OrderProducerTest {

    @Autowired
    private OrderProducer orderProducer;

    @Autowired
    private NewTopic topic;

    @MockitoBean
    private KafkaTemplate<String, Order> kafkaTemplate;

    @Captor
    private ArgumentCaptor<Message<OrderEvent>> messageCaptor;

    @Test
    void shouldSendMessage() {
        assertEquals("test_topic", topic.name());

        OrderEvent orderEvent = new OrderEvent();
        orderEvent.setMessage("testMessage");
        orderEvent.setStatus(OrderStatus.PENDING);

        Order order = new Order();
        order.setQuantity(1);
        order.setPrice(23.45);
        order.setName("testOrder");
        order.setOrderId("randomUUID");
        orderEvent.setOrder(order);

        orderProducer.sendMessage(orderEvent);

        verify(kafkaTemplate, times(1)).send(messageCaptor.capture());

        Message<OrderEvent> messageCaptured = messageCaptor.getValue();
        assertNotNull(messageCaptured);
        OrderEvent orderEventCaptured = messageCaptured.getPayload();
        assertEquals(orderEvent, orderEventCaptured);
    }

}