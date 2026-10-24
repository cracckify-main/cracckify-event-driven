package com.eazybytes.accounts.event;

import com.eazybytes.accounts.dto.AccountsDto;
import com.eazybytes.accounts.entity.Accounts;
import com.eazybytes.accounts.mapper.AccountsMapper;
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
public class AccountsEventPublisher {

    private final KafkaTemplate<String, BankDataChangedEvent> kafkaTemplate;
    private final ObjectMapper objectMapper;

    @Value("${app.kafka.topics.events}")
    private String topic;

    public void publish(Accounts account, BankDataChangedEvent.ChangeType changeType) {
        AccountsDto snapshot = AccountsMapper.mapToAccountsDto(account, new AccountsDto());

        BankDataChangedEvent event = new BankDataChangedEvent(
                UUID.randomUUID().toString(),
                BankDataChangedEvent.ServiceName.ACCOUNTS,
                changeType,
                account.getMobileNumber(),
                account.getEventRevision(),
                Instant.now(),
                objectMapper.valueToTree(snapshot)
        );

        try {
            kafkaTemplate.send(topic, account.getMobileNumber(), event).get();
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Interrupted while publishing account event", exception);
        } catch (ExecutionException exception) {
            throw new IllegalStateException("Could not publish account event", exception);
        }
    }
}
