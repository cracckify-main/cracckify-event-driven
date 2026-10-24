package com.eazybytes.cards.event;

import com.eazybytes.cards.dto.CardsDto;
import com.eazybytes.cards.entity.Cards;
import com.eazybytes.cards.mapper.CardsMapper;
import com.eazybytes.common.event.BankDataChangedEvent;
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
public class CardsEventPublisher {

    private final KafkaTemplate<String, BankDataChangedEvent> kafkaTemplate;
    private final ObjectMapper objectMapper;

    @Value("${app.kafka.topics.cards-events}")
    private String topic;

    public void publish(Cards card, BankDataChangedEvent.ChangeType changeType) {
        CardsDto snapshot = CardsMapper.mapToCardsDto(card, new CardsDto());

        BankDataChangedEvent event = new BankDataChangedEvent(
                UUID.randomUUID().toString(),
                BankDataChangedEvent.ServiceName.CARDS,
                changeType,
                card.getMobileNumber(),
                card.getEventRevision(),
                Instant.now(),
                objectMapper.valueToTree(snapshot)
        );

        try {
            kafkaTemplate.send(topic, card.getMobileNumber(), event).get();
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Interrupted while publishing cards event", exception);
        } catch (ExecutionException exception) {
            throw new IllegalStateException("Could not publish cards event", exception);
        }
    }
}
