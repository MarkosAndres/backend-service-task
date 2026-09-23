package com.marcos.incode_home_task.service;

import com.marcos.incode_home_task.company.Company;
import com.marcos.incode_home_task.dto.FreeCompanyResponse;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Objects;

@Service
public class FreeThirdPartyClient
{
    private final RestClient restClient;
    private final PremiumThirdPartyClient premiumThirdPartyClient;

    public FreeThirdPartyClient(
            @Value("${third-party.base-url}") String thirdPartyBaseUrl,
            PremiumThirdPartyClient premiumThirdPartyClient)
    {
        restClient = RestClient.builder()
                .baseUrl(thirdPartyBaseUrl)
                .build();
        this.premiumThirdPartyClient = premiumThirdPartyClient;
    }

    @CircuitBreaker(name = "freeThirdParty", fallbackMethod = "fallbackToPremium")
    public List<Company> findResults(String query)
    {
        List<FreeCompanyResponse> responseBody = restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/free-third-party")
                        .queryParam("query", query)
                        .build())
                .retrieve()
                .body(new ParameterizedTypeReference<>(){});

        return Objects.requireNonNull(responseBody, "Free third-party response body must not be null")
                .stream()
                .map(response ->
                        new Company(
                                response.cin(),
                                response.name(),
                                response.registration_date(),
                                response.address(),
                                response.is_active()))
                .toList();
    }

    public List<Company> fallbackToPremium(String query, Throwable throwable)
    {
        return premiumThirdPartyClient.findResults(query);
    }

}
