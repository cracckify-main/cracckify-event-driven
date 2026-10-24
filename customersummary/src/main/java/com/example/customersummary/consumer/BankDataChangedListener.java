package com.example.customersummary.consumer;

import com.eazybytes.common.event.BankDataChangedEvent;
import com.example.customersummary.service.CustomerSummaryProjectionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class BankDataChangedListener {

    private static final Logger log =
            LoggerFactory.getLogger(BankDataChangedListener.class);

    private final CustomerSummaryProjectionService projectionService;

    public BankDataChangedListener(
            CustomerSummaryProjectionService projectionService
    ) {
        this.projectionService = projectionService;
    }

    @KafkaListener(topics = {
            "${app.kafka.topics.customer-events}",
            "${app.kafka.topics.accounts-events}",
            "${app.kafka.topics.cards-events}",
            "${app.kafka.topics.loans-events}"
    })
    public void consume(BankDataChangedEvent event) {
        log.info(
                "Received {} event for mobile number {} at revision {}",
                event.source(),
                event.mobileNumber(),
                event.revision()
        );

        projectionService.apply(event);
    }
}