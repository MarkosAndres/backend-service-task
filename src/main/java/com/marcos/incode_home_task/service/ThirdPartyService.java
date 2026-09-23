package com.marcos.incode_home_task.service;

import com.marcos.incode_home_task.company.Company;
import com.marcos.incode_home_task.dto.FreeCompanyResponse;
import com.marcos.incode_home_task.dto.PremiumCompanyResponse;
import com.marcos.incode_home_task.exception.ThirdPartyServiceUnavailableException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Objects;

@Service
public class ThirdPartyService
{
    private final RestClient restClient;

    public ThirdPartyService(@Value("${third-party.base-url}") String thirdPartyBaseUrl)
    {
        restClient = RestClient
                .builder()
                .baseUrl(thirdPartyBaseUrl)
                .build();
    }

    @CircuitBreaker(name = "freeThirdParty", fallbackMethod = "fallbackToPremium")
    public List<Company> findFreeResults(String query)
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
        return findPremiumResults(query);
    }

    @CircuitBreaker(name = "premiumThirdParty", fallbackMethod = "handlePremiumFailure")
    public List<Company> findPremiumResults(String query)
    {
        List<PremiumCompanyResponse> responseBody = restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/premium-third-party")
                        .queryParam("query", query)
                        .build())
                .retrieve()
                .body(new ParameterizedTypeReference<>(){});

        return Objects.requireNonNull(responseBody, "Premium third-party response body must not be null")
                .stream()
                .map(response ->
                        new Company(
                                response.companyIdentificationNumber(),
                                response.companyName(),
                                response.registrationDate(),
                                response.companyFullAddress(),
                                response.isActive()))
                .toList();
    }

    public List<Company> handlePremiumFailure(String query, Throwable throwable)
    {
        throw new ThirdPartyServiceUnavailableException(throwable);
    }
}
