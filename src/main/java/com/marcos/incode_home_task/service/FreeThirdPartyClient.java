package com.marcos.incode_home_task.service;

import com.marcos.incode_home_task.company.Company;
import com.marcos.incode_home_task.dto.FreeCompanyResponse;
import com.marcos.incode_home_task.exception.NoRecordsFoundException;
import com.marcos.incode_home_task.exception.ThirdPartyServiceException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.Arrays;
import java.util.List;

@Service
public class FreeThirdPartyClient
{
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
                    .toList();

            if (companies.isEmpty())
            {
                throw new NoRecordsFoundException();
            }
            return companies;
        }
        catch (Exception exception)
        {
            throw new ThirdPartyServiceException(exception);
        }
    }
}
