package com.example.customersummary.dto;

import java.util.Map;

public record CustomerSummaryResponse(
        Map<String, Object> customerDto,
        Map<String, Object> cardsDto,
        Map<String, Object> loansDto,
        Map<String, Object> accountsDto
) {
}
