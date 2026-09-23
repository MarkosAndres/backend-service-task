package com.marcos.incode_home_task.service;

import com.marcos.incode_home_task.company.Company;
import com.marcos.incode_home_task.dto.PremiumCompanyResponse;
import com.marcos.incode_home_task.exception.ThirdPartyServiceException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.Arrays;
import java.util.List;

@Service
public class PremiumThirdPartyClient
{
    private final RestClient restClient;

    public PremiumThirdPartyClient(@Value("${third-party.base-url}") String thirdPartyBaseUrl)
    {
        restClient = RestClient.builder()
                .baseUrl(thirdPartyBaseUrl)
                .build();
    }

    public List<Company> findResults(String query)
            throws ThirdPartyServiceException
    {
        try
        {
            PremiumCompanyResponse[] responseBody = restClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/premium-third-party")
                            .queryParam("query", query)
                            .build())
                    .retrieve()
                    .body(PremiumCompanyResponse[].class);

            return responseBody == null
                    ? List.of()
                    : Arrays.stream(responseBody)
                    .map(response ->
                            new Company(
                                    response.companyIdentificationNumber(),
                                    response.companyName(),
                                    response.registrationDate(),
                                    response.companyFullAddress(),
                                    response.isActive()))
                    .toList();
        }
        catch (Exception exception)
        {
            throw new ThirdPartyServiceException(exception);
        }
    }
}
