package com.example.customersummary.service;


import com.eazybytes.common.event.BankDataChangedEvent;
import com.example.customersummary.documents.CustomerSummaryDocument;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Objects;

@Service
public class CustomerSummaryProjectionService {

    private static final TypeReference<Map<String, Object>> MAP_TYPE =
            new TypeReference<>() {};

    private final MongoTemplate mongoTemplate;
    private final ObjectMapper objectMapper;

    public CustomerSummaryProjectionService(
            MongoTemplate mongoTemplate,
            ObjectMapper objectMapper
    ) {
        this.mongoTemplate = mongoTemplate;
        this.objectMapper = objectMapper;
    }

    public void apply(BankDataChangedEvent event) {
        String mobileNumber = event.mobileNumber();
        SectionFields fields = fieldsFor(event.source());

        Map<String, Object> snapshot = objectMapper.convertValue(
                Objects.requireNonNull(event.payload(), "Event payload is required"),
                MAP_TYPE
        );

        Query documentQuery = Query.query(
                Criteria.where("mobileNumber").is(mobileNumber)
        );

        // Create the per-customer document on the first event.
        // Revision values start at zero; producer revisions start at one.
        Update initializeDocument = new Update()
                .setOnInsert("customerRevision", 0L)
                .setOnInsert("accountsRevision", 0L)
                .setOnInsert("cardsRevision", 0L)
                .setOnInsert("loansRevision", 0L);

        mongoTemplate.upsert(
                documentQuery,
                initializeDocument,
                CustomerSummaryDocument.class
        );

        // Apply this section only if its incoming revision is newer.
        Query newerEventQuery = Query.query(
                Criteria.where("mobileNumber").is(mobileNumber)
                        .and(fields.revisionField())
                        .lt(event.revision())
        );

        Update sectionUpdate = new Update()
                .set(fields.dataField(), snapshot)
                .set(fields.revisionField(), event.revision());

        mongoTemplate.updateFirst(
                newerEventQuery,
                sectionUpdate,
                CustomerSummaryDocument.class
        );
    }

    private SectionFields fieldsFor(BankDataChangedEvent.ServiceName source) {
        return switch (source) {
            case CUSTOMER -> new SectionFields("customer", "customerRevision");
            case ACCOUNTS -> new SectionFields("accounts", "accountsRevision");
            case CARDS -> new SectionFields("cards", "cardsRevision");
            case LOANS -> new SectionFields("loans", "loansRevision");
        };
    }

    private record SectionFields(String dataField, String revisionField) {
    }
}