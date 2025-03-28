package com.xfef0.email_service.kafka;

import com.xfef0.base_domains.OrderStatus;
import com.xfef0.base_domains.dto.Order;
import com.xfef0.base_domains.dto.OrderEvent;
import jakarta.jms.JMSException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@SpringBootTest
@DirtiesContext
@EmbeddedKafka(partitions = 1, brokerProperties = {"listeners=PLAINTEXT://localhost:9092", "port=9092"})
class EmailProducerTest {

    @Autowired
    private EmailProducer emailProducer;

    @MockitoSpyBean
    private JmsTemplate jmsTemplate;

    @Test
    void shouldPublishActiveMQMessage() throws JMSException {
        OrderEvent orderEvent = new OrderEvent();
        orderEvent.setStatus(OrderStatus.PENDING);
        orderEvent.setMessage("test message");
        Order order = new Order();
        order.setOrderId("randomUUID");
        order.setName("testName");
        order.setQuantity(2);
        order.setPrice(33.4);
        orderEvent.setOrder(order);

        emailProducer.publishEmailMessage(orderEvent);

        verify(jmsTemplate, times(1)).convertAndSend(anyString(), anyString());
    }

}