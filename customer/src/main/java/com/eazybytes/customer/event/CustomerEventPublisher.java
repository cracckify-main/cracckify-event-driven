package com.eazybytes.customer.event;

import com.eazybytes.common.event.BankDataChangedEvent;
import com.eazybytes.customer.dto.CustomerDto;
import com.eazybytes.customer.entity.Customer;
import com.eazybytes.customer.mapper.CustomerMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

@Component
@RequiredArgsConstructor
public class CustomerEventPublisher {

    private final KafkaTemplate<String, BankDataChangedEvent> kafkaTemplate;
    private final ObjectMapper objectMapper;

    @Value("${app.kafka.topics.customer-events}")
    private String topic;

    public void publish(Customer customer, BankDataChangedEvent.ChangeType changeType) {
        CustomerDto snapshot =
                CustomerMapper.mapToCustomerDto(customer, new CustomerDto());

        BankDataChangedEvent event = new BankDataChangedEvent(
                UUID.randomUUID().toString(),
                BankDataChangedEvent.ServiceName.CUSTOMER,
                changeType,
                customer.getMobileNumber(),
                customer.getEventRevision(),
                Instant.now(),
                objectMapper.valueToTree(snapshot)
        );

        try {
            kafkaTemplate.send(topic, customer.getMobileNumber(), event).get();
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Interrupted while publishing customer event", exception);
        } catch (ExecutionException exception) {
            throw new IllegalStateException("Could not publish customer event", exception);
        }
    }
}