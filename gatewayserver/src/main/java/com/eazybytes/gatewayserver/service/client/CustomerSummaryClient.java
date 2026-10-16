package com.eazybytes.gatewayserver.service.client;

import com.eazybytes.gatewayserver.dto.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.GetExchange;
import reactor.core.publisher.Mono;

public interface CustomerSummaryClient {

    @GetExchange(value = "/eazybank/customer/api/fetch", accept = "application/json")
    Mono<ResponseEntity<CustomerDto>> getCustomerDetails(@RequestParam("mobileNumber") String mobileNumber);

    @GetExchange(value = "/eazybank/loans/api/fetch", accept = "application/json")
    Mono<ResponseEntity<LoansDto>> getCustomerLoanDetails(@RequestParam("mobileNumber") String mobileNumber);

    @GetExchange(value = "/eazybank/cards/api/fetch", accept = "application/json")
    Mono<ResponseEntity<CardsDto>> getCustomerCardsDetails(@RequestParam("mobileNumber") String mobileNumber);

    @GetExchange(value = "/eazybank/accounts/api/fetch", accept = "application/json")
    Mono<ResponseEntity<AccountsDto>> getCustomerAccountsDetails(@RequestParam("mobileNumber") String mobileNumber);
}
