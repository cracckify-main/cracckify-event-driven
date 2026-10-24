package com.eazybytes.loans.event;

import com.eazybytes.common.event.BankDataChangedEvent;
import com.eazybytes.loans.dto.LoansDto;
import com.eazybytes.loans.entity.Loans;
import com.eazybytes.loans.mapper.LoansMapper;
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
public class LoansEventPublisher {

    private final KafkaTemplate<String, BankDataChangedEvent> kafkaTemplate;
    private final ObjectMapper objectMapper;

    @Value("${app.kafka.topics.events}")
    private String topic;

    public void publish(Loans loan, BankDataChangedEvent.ChangeType changeType) {
        LoansDto snapshot = LoansMapper.mapToLoansDto(loan, new LoansDto());

        BankDataChangedEvent event = new BankDataChangedEvent(
                UUID.randomUUID().toString(),
                BankDataChangedEvent.ServiceName.LOANS,
                changeType,
                loan.getMobileNumber(),
                loan.getEventRevision(),
                Instant.now(),
                objectMapper.valueToTree(snapshot)
        );

        try {
            kafkaTemplate.send(topic, loan.getMobileNumber(), event).get();
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Interrupted while publishing loan event", exception);
        } catch (ExecutionException exception) {
            throw new IllegalStateException("Could not publish loan event", exception);
        }
    }
}
