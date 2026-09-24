package com.marcos.incode_home_task.service;

import com.marcos.incode_home_task.dto.Company;
import com.marcos.incode_home_task.config.VerificationContext;
import com.marcos.incode_home_task.dto.FreeCompanyResponse;
import com.marcos.incode_home_task.exception.ThirdPartyServiceException;
import com.marcos.incode_home_task.metrics.ApplicationMetrics;
import com.marcos.incode_home_task.dto.ThirdPartySearchResult;
import com.marcos.incode_home_task.dto.VerificationSource;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.micrometer.core.annotation.Timed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
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
    private final PremiumThirdPartyClient premiumThirdPartyClient;
    private final ApplicationMetrics applicationMetrics;

    public FreeThirdPartyClient(
            @Value("${third-party.base-url}") String thirdPartyBaseUrl,
            PremiumThirdPartyClient premiumThirdPartyClient,
            ApplicationMetrics applicationMetrics)
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
        this.premiumThirdPartyClient = premiumThirdPartyClient;
        this.applicationMetrics = applicationMetrics;
    }

    @CircuitBreaker(name = "freeThirdParty", fallbackMethod = "findResultsFromPremium")
    @Timed(value = "third.party.request.duration", extraTags = {"provider", "free"})
    public ThirdPartySearchResult findResults(String query)
            throws ThirdPartyServiceException
    {
        log.info("Calling free third-party provider");
        applicationMetrics.thirdPartyRequestCalled(VerificationSource.FREE);
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

            log.info("Free third-party provider returned results: resultCount={}", companies.size());
            return new ThirdPartySearchResult(companies, VerificationSource.FREE);
        }
        catch (Exception exception)
        {
            log.warn("Free third-party provider call failed", exception);
            applicationMetrics.thirdPartyRequestFailed(VerificationSource.FREE);
            throw new ThirdPartyServiceException(exception, VerificationSource.FREE);
        }
    }

    public ThirdPartySearchResult findResultsFromPremium(String query, Throwable exception)
            throws ThirdPartyServiceException
    {
        log.warn("Free circuit breaker denied the call; using Premium fallback", exception);
        applicationMetrics.thirdPartyFallbackCalled(VerificationSource.FREE);
        return premiumThirdPartyClient.findResults(query);
    }
}
