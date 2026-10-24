package com.eazybytes.common.event;


import com.fasterxml.jackson.databind.JsonNode;

import java.time.Instant;

public record BankDataChangedEvent(
        String eventId,
        ServiceName source,
        ChangeType changeType,
        String mobileNumber,
        long revision,
        Instant occurredAt,
        JsonNode payload
) {
    public enum ServiceName {
        CUSTOMER,
        ACCOUNTS,
        CARDS,
        LOANS
    }

    public enum ChangeType {
        CREATED,
        UPDATED,
        DEACTIVATED
    }
}
