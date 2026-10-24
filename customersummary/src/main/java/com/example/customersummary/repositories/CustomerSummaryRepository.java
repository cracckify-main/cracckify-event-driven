package com.example.customersummary.repositories;

import com.example.customersummary.documents.CustomerSummaryDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface CustomerSummaryRepository
        extends MongoRepository<CustomerSummaryDocument, String> {
}
