package com.marcos.incode_home_task.service;

import com.marcos.incode_home_task.company.Company;
import com.marcos.incode_home_task.dto.PremiumCompanyResponse;
import com.marcos.incode_home_task.exception.ThirdPartyServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.Arrays;
import java.util.List;

@Service
public class PremiumThirdPartyClient
{
    private static final Logger log = LoggerFactory.getLogger(PremiumThirdPartyClient.class);

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
        log.info("Calling premium third-party provider: query={}", query);
        try
        {
            PremiumCompanyResponse[] responseBody = restClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/premium-third-party")
                            .queryParam("query", query)
                            .build())
                    .retrieve()
                    .body(PremiumCompanyResponse[].class);

            List<Company> companies = responseBody == null
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
            log.info("Premium third-party provider returned results: query={}, resultCount={}", query, companies.size());
            return companies;
        }
        catch (Exception exception)
        {
            log.warn("Premium third-party provider call failed: query={}", query, exception);
            throw new ThirdPartyServiceException(exception);
        }
    }
}
