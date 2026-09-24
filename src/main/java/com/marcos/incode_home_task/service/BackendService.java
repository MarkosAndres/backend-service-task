package com.marcos.incode_home_task.service;

import com.marcos.incode_home_task.company.Company;
import com.marcos.incode_home_task.dto.BackendResponse;
import com.marcos.incode_home_task.dto.CompanyResponse;
import com.marcos.incode_home_task.dto.SearchResult;
import com.marcos.incode_home_task.exception.ThirdPartyServiceException;
import com.marcos.incode_home_task.verification.ThirdPartySearchResult;
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

    public BackendService(ThirdPartyService thirdPartyService,
                          VerificationService verificationService)
    {
        this.thirdPartyService = thirdPartyService;
        this.verificationService = verificationService;
    }

    public BackendResponse search(UUID verificationId, String query)
    {
        Instant requestTimestamp = Instant.now();
        log.info("Searching companies: verificationId={}, query={}", verificationId, query);

        try
        {
            ThirdPartySearchResult thirdPartyResult = thirdPartyService.findCompanies(query);
            List<Company> companiesFound = thirdPartyResult.companies();

            if (companiesFound.isEmpty())
            {
                return this.emptyResponse(thirdPartyResult, verificationId, query, requestTimestamp);
            }

            log.info("Found active companies: verificationId={}, query={}, resultCount={}",
                    verificationId, query, companiesFound.size());

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
        log.info("No active companies found: verificationId={}, query={}", verificationId, query);
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
        log.info("All third party services failed: verificationId={}, query={}", verificationId, query);
        BackendResponse response = new BackendResponse(
                verificationId,
                query,
                SearchResult.unavailable());
        verificationService.store(response, thirdPartyServiceException.source(), requestTimestamp);
        return response;
    }
}
