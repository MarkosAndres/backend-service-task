package com.marcos.incode_home_task.service;

import com.marcos.incode_home_task.dto.Company;
import com.marcos.incode_home_task.dto.BackendResponse;
import com.marcos.incode_home_task.dto.CompanyResponse;
import com.marcos.incode_home_task.dto.SearchResult;
import com.marcos.incode_home_task.exception.ThirdPartyServiceException;
import com.marcos.incode_home_task.dto.ThirdPartySearchResult;
import com.marcos.incode_home_task.metrics.ApplicationMetrics;
import io.micrometer.core.annotation.Counted;
import io.micrometer.core.annotation.Timed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
public class BackendService
{
    private static final Logger log = LoggerFactory.getLogger(BackendService.class);

    private final ThirdPartyService thirdPartyService;
    private final VerificationService verificationService;
    private final ApplicationMetrics applicationMetrics;

    public BackendService(ThirdPartyService thirdPartyService,
                          VerificationService verificationService,
                          ApplicationMetrics applicationMetrics)
    {
        this.thirdPartyService = thirdPartyService;
        this.verificationService = verificationService;
        this.applicationMetrics = applicationMetrics;
    }

    @Timed(value = "backend.search.duration")
    @Counted(value = "backend.search.calls")
    public BackendResponse search(UUID verificationId, String query)
    {
        Instant requestTimestamp = Instant.now();
        log.info("Searching companies");

        try
        {
            ThirdPartySearchResult thirdPartyResult = thirdPartyService.findCompanies(query);
            List<Company> companiesFound = thirdPartyResult.companies();

            if (companiesFound.isEmpty())
            {
                return this.emptyResponse(thirdPartyResult, verificationId, query, requestTimestamp);
            }

            log.info("Found active companies: resultCount={}", companiesFound.size());

            Company firstCompanyResult = companiesFound.getFirst();
            List<CompanyResponse> otherResults = companiesFound
                    .subList(1, companiesFound.size())
                    .stream()
                    .map(CompanyResponse::from)
                    .toList();

            BackendResponse response = new BackendResponse(
                    verificationId,
                    query,
                    SearchResult.found(
                            CompanyResponse.from(firstCompanyResult),
                            otherResults));
            verificationService.store(response, thirdPartyResult.source(), requestTimestamp);
            return response;
        }
        catch (ThirdPartyServiceException thirdPartyServiceException)
        {
            return unavailableServices(
                    thirdPartyServiceException,
                    verificationId,
                    query,
                    requestTimestamp);
        }
    }

    private BackendResponse emptyResponse(ThirdPartySearchResult thirdPartyResult, UUID verificationId, String query, Instant requestTimestamp)
    {
        log.info("No active companies found");
        BackendResponse response = new BackendResponse(
                verificationId,
                query,
                SearchResult.noResults());
        verificationService.store(response, thirdPartyResult.source(), requestTimestamp);
        return response;
    }

    private BackendResponse unavailableServices(
            ThirdPartyServiceException thirdPartyServiceException,
            UUID verificationId,
            String query,
            Instant requestTimestamp)
    {
        log.info("All third party services failed");
        applicationMetrics.backendSearchFailed();

        BackendResponse response = new BackendResponse(
                verificationId,
                query,
                SearchResult.unavailable());
        verificationService.store(response, thirdPartyServiceException.source(), requestTimestamp);
        return response;
    }
}
