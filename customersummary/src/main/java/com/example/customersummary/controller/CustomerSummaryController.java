package com.example.customersummary.controller;

import com.example.customersummary.documents.CustomerSummaryDocument;
import com.example.customersummary.dto.CustomerSummaryResponse;
import com.example.customersummary.repositories.CustomerSummaryRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

import static org.springframework.http.ResponseEntity.notFound;

@RestController
@RequestMapping("/api")
public class CustomerSummaryController {

    private final CustomerSummaryRepository repository;

    public CustomerSummaryController(CustomerSummaryRepository repository) {
        this.repository = repository;
    }

    @GetMapping("/fetch")
    public ResponseEntity<CustomerSummaryResponse> fetchCustomerSummary(
            @RequestParam("mobileNumber") String mobileNumber) {
        CustomerSummaryDocument document = repository.findById(mobileNumber)
                .orElseThrow(() -> notFound(mobileNumber));

        if (!isActive(document.customer())
                || !isActive(document.cards())
                || !isActive(document.loans())
                || !isActive(document.accounts())) {
            throw notFound(mobileNumber);
        }

        CustomerSummaryResponse customerSummaryResponse = new CustomerSummaryResponse(document.customer(),
                document.cards(),
                document.loans(),
                document.accounts());

        return ResponseEntity.status(HttpStatus.OK).body(customerSummaryResponse);

    }

    private boolean isActive(Map<String, Object> section) {
        return section != null && Boolean.TRUE.equals(section.get("activeSw"));
    }

    private ResponseStatusException notFound(String mobileNumber) {
        return new ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "Complete active customer summary not found for mobile number " + mobileNumber
        );
    }
}
