package com.eazybytes.gatewayserver.handler;

import com.eazybytes.gatewayserver.dto.*;
import com.eazybytes.gatewayserver.service.client.CustomerSummaryClient;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple4;

@Component
@RequiredArgsConstructor
public class CustomerCompositeHandler {
    private final CustomerSummaryClient client;

    public Mono<ServerResponse> fetchCustomerSummary (ServerRequest serverRequest){
        String mobileNumber = serverRequest.queryParam("mobileNumber").get();
        Mono<ResponseEntity<AccountsDto>> customerAccountsDetails = client.getCustomerAccountsDetails(mobileNumber);
        Mono<ResponseEntity<CustomerDto>> customerDetails = client.getCustomerDetails(mobileNumber);
        Mono<ResponseEntity<LoansDto>> customerLoanDetails =   client.getCustomerLoanDetails(mobileNumber);
        Mono<ResponseEntity<CardsDto>> customerCardsDetails = client.getCustomerCardsDetails(mobileNumber);

        Mono<Tuple4<ResponseEntity<AccountsDto>, ResponseEntity<CustomerDto>, ResponseEntity<LoansDto>, ResponseEntity<CardsDto>>> x = Mono.zip(customerAccountsDetails, customerDetails, customerLoanDetails, customerCardsDetails);
        return Mono.zip(customerAccountsDetails, customerDetails, customerLoanDetails, customerCardsDetails)
                .flatMap(tuple -> {
                    AccountsDto accounts = tuple.getT1().getBody();
                    CustomerDto customer = tuple.getT2().getBody();
                    LoansDto loans = tuple.getT3().getBody();
                    CardsDto cards = tuple.getT4().getBody();

                    CustomerSummaryDto summaryDto = new CustomerSummaryDto(customer, cards, loans, accounts);

                    return ServerResponse.ok()
                            .contentType(MediaType.APPLICATION_JSON)
                            .bodyValue(summaryDto);
                });
    }
}
