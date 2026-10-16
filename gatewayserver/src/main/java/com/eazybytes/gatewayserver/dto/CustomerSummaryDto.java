package com.eazybytes.gatewayserver.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CustomerSummaryDto {
    private CustomerDto customerDto;
    private CardsDto cardsDto;
    private LoansDto loansDto;
    private AccountsDto accountsDto;
}
