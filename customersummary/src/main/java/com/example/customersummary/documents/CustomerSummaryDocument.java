
package com.example.customersummary.documents;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Map;

@Document(collection = "customer_summaries")
public record CustomerSummaryDocument(
        @Id String mobileNumber,
        Map<String, Object> customer,
        Map<String, Object> accounts,
        Map<String, Object> cards,
        Map<String, Object> loans,
        long customerRevision,
        long accountsRevision,
        long cardsRevision,
        long loansRevision
) {
}