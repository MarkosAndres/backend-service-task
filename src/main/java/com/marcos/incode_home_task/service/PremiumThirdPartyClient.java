package com.marcos.incode_home_task.service;

import com.marcos.incode_home_task.company.Company;
import com.marcos.incode_home_task.config.VerificationContext;
import com.marcos.incode_home_task.dto.PremiumCompanyResponse;
import com.marcos.incode_home_task.exception.ThirdPartyServiceException;
import com.marcos.incode_home_task.verification.ThirdPartySearchResult;
import com.marcos.incode_home_task.verification.VerificationSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
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
                .requestInterceptor((request, body, execution) ->
                {
                    String verificationId = MDC.get(VerificationContext.MDC_KEY);
                    if (verificationId != null)
                    {
                        request.getHeaders().set(VerificationContext.HEADER_NAME, verificationId);
                    }
                    String query = MDC.get(VerificationContext.QUERY_MDC_KEY);
                    if (query != null)
                    {
                        request.getHeaders().set(VerificationContext.QUERY_HEADER_NAME, query);
                    }
                    return execution.execute(request, body);
                })
                .build();
    }

    public ThirdPartySearchResult findResults(String query)
            throws ThirdPartyServiceException
    {
        log.info("Calling premium third-party provider");
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
                    .filter(Company::active)
                    .toList();
            log.info("Premium third-party provider returned results: resultCount={}", companies.size());
            return new ThirdPartySearchResult(companies, VerificationSource.PREMIUM);
        }
        catch (Exception exception)
        {
            log.warn("Premium third-party provider call failed", exception);
            throw new ThirdPartyServiceException(exception, VerificationSource.PREMIUM);
        }
    }
}
