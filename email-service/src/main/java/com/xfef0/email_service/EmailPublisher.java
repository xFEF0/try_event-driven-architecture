package com.xfef0.email_service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Slf4j
@Component
public class EmailPublisher {

    @JmsListener(destination = "${spring.activemq.queue.name}")
    public void consumeMessage(String message) {
        log.info("Pretend to send an email containing --> {}", message);
    }
}
