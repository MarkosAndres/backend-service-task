package com.marcos.incode_home_task.service;

import com.marcos.incode_home_task.company.Company;
import com.marcos.incode_home_task.dto.FreeCompanyResponse;
import com.marcos.incode_home_task.exception.ThirdPartyServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.Arrays;
import java.util.List;

@Service
public class FreeThirdPartyClient
{
    private static final Logger log = LoggerFactory.getLogger(FreeThirdPartyClient.class);

    private final RestClient restClient;

    public FreeThirdPartyClient(
            @Value("${third-party.base-url}") String thirdPartyBaseUrl)
    {
        restClient = RestClient.builder()
                .baseUrl(thirdPartyBaseUrl)
                .build();
    }

    public List<Company> findResults(String query)
            throws ThirdPartyServiceException
    {
        log.info("Calling free third-party provider: query={}", query);
        try
        {
            FreeCompanyResponse[] responseBody = restClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/free-third-party")
                            .queryParam("query", query)
                            .build())
                    .retrieve()
                    .body(FreeCompanyResponse[].class);

            List<Company> companies = (responseBody == null)
                    ? List.of()
                    : Arrays.stream(responseBody)
                    .map(response ->
                            new Company(
                                    response.cin(),
                                    response.name(),
                                    response.registration_date(),
                                    response.address(),
                                    response.is_active()))
                    .filter(Company::active)
                    .toList();

            log.info("Free third-party provider returned results: query={}, resultCount={}", query, companies.size());
            return companies;
        }
        catch (Exception exception)
        {
            log.warn("Free third-party provider call failed: query={}", query, exception);
            throw new ThirdPartyServiceException(exception);
        }
    }
}
