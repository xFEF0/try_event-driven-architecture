package com.xfef0.stock_service.kafka;

import com.xfef0.base_domains.OrderStatus;
import com.xfef0.base_domains.dto.Order;
import com.xfef0.base_domains.dto.OrderEvent;
import com.xfef0.stock_service.entity.OrderEntity;
import com.xfef0.stock_service.repository.OrderRepository;
import io.zonky.test.db.AutoConfigureEmbeddedDatabase;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;

import java.time.LocalDateTime;
import java.util.Optional;

import static io.zonky.test.db.AutoConfigureEmbeddedDatabase.DatabaseProvider.ZONKY;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@DirtiesContext
@AutoConfigureEmbeddedDatabase(provider = ZONKY)
@EmbeddedKafka(partitions = 1, brokerProperties = {"listeners=PLAINTEXT://localhost:9092", "port=9092"})
class OrderConsumerTest {

    @Autowired
    private OrderConsumer orderConsumer;

    @Autowired
    private OrderRepository orderRepository;

    @Captor
    private ArgumentCaptor<OrderEntity> entityCaptor;

    @Test
    void shouldSaveOrderInDB() {
        LocalDateTime startTestTime = LocalDateTime.now();

        Order order = new Order("randomUUID",
                "orderUnderTest",
                2,
                34.5
        );
        OrderEvent orderEvent = new OrderEvent("testEvent",
                OrderStatus.PENDING,
                order
        );

        assertTrue(orderRepository.findById(order.getOrderId()).isEmpty());

        orderConsumer.consume(orderEvent);

        Optional<OrderEntity> optionalOrderEntity = orderRepository.findById(order.getOrderId());
        assertTrue(optionalOrderEntity.isPresent());
        OrderEntity orderEntity = optionalOrderEntity.get();
        assertNotNull(orderEntity);
        assertEquals(order.getOrderId(), orderEntity.getId());
        assertEquals(order.getName(), orderEntity.getName());
        assertEquals(order.getPrice(), orderEntity.getPrice());
        assertEquals(order.getQuantity(), orderEntity.getQuantity());
        assertEquals(orderEvent.getStatus(), orderEntity.getStatus());
        assertTrue(orderEntity.getLastModification().isAfter(startTestTime));


    }
}