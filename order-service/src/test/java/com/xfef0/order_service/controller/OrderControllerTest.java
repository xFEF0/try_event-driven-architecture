package com.xfef0.order_service.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.xfef0.base_domains.dto.Order;
import com.xfef0.base_domains.dto.OrderEvent;
import com.xfef0.order_service.kafka.OrderProducer;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(OrderController.class)
class OrderControllerTest {

    private static final String BASE_URL = "/api/v1/orders";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private OrderProducer orderProducer;

    @Captor
    private ArgumentCaptor<OrderEvent> orderEventCaptor;

    @Test
    void shouldPlaceOrder() throws Exception {
        Order order = new Order();
        order.setName("test order");
        order.setQuantity(3);
        order.setPrice(25.5);

        mockMvc.perform(MockMvcRequestBuilders.post(BASE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(order)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$").value("Order placed"));

        verify(orderProducer, times(1)).sendMessage(orderEventCaptor.capture());

        OrderEvent captorValue = orderEventCaptor.getValue();
        assertNotNull(captorValue);
        Order capturedOrder = captorValue.getOrder();
        assertNotNull(capturedOrder);
        assertNotNull(capturedOrder.getOrderId());
    }
}