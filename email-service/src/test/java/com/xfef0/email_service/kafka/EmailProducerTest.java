package com.xfef0.email_service.kafka;

import com.xfef0.base_domains.OrderStatus;
import com.xfef0.base_domains.dto.Order;
import com.xfef0.base_domains.dto.OrderEvent;
import com.xfef0.email_service.config.TestConfiguration;
import jakarta.jms.JMSException;
import jakarta.jms.Message;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.util.ReflectionTestUtils;

import static com.xfef0.email_service.config.TestConfiguration.QUEUE_NAME;
import static com.xfef0.email_service.config.TestConfiguration.embeddedBroker;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ContextConfiguration(classes = {TestConfiguration.class, EmailProducer.class})
@DirtiesContext
@EmbeddedKafka(partitions = 1, brokerProperties = {"listeners=PLAINTEXT://localhost:9092", "port=9092"})
class EmailProducerTest {

    @Autowired
    private JmsTemplate jmsTemplate;

    private EmailProducer emailProducer;

    @BeforeEach
    void setUp() {
        embeddedBroker.start();
        this.emailProducer = new EmailProducer(jmsTemplate);
        ReflectionTestUtils.setField(emailProducer, "destinationQueue", QUEUE_NAME);
    }

    @AfterEach
    void tearDown() {
        embeddedBroker.stop();
    }

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

        assertEquals(0, embeddedBroker.getDestination(QUEUE_NAME).getDestinationStatistics().getMessages().getCount());

        emailProducer.publishEmailMessage(orderEvent);

        assertEquals(1, embeddedBroker.getMessageCount(QUEUE_NAME));
        Message message = embeddedBroker.peekMessage(QUEUE_NAME);
        assertNotNull(message);
        String messageBody = message.getBody(String.class);
        assertTrue(messageBody.contains("name=" + order.getName()));
        assertTrue(messageBody.contains("quantity=" + order.getQuantity()));
        assertTrue(messageBody.contains("price=" + order.getPrice()));
    }

}