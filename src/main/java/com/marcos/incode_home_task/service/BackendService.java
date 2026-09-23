package com.marcos.incode_home_task.service;

import com.marcos.incode_home_task.company.Company;
import com.marcos.incode_home_task.dto.BackendResponse;
import com.marcos.incode_home_task.dto.CompanyResponse;
import com.marcos.incode_home_task.dto.SearchResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class BackendService
{
    private static final Logger log = LoggerFactory.getLogger(BackendService.class);

    private final ThirdPartyService thirdPartyService;

    public BackendService(ThirdPartyService thirdPartyService)
    {
        this.thirdPartyService = thirdPartyService;
    }

    public BackendResponse search(UUID verificationId, String query)
    {
        List<Company> companiesFound = thirdPartyService.findCompanies(query);

        if (companiesFound.isEmpty())
        {
            log.info("No active companies found: verificationId={}, query={}", verificationId, query);
            return new BackendResponse(
                    verificationId,
                    query,
                    SearchResult.noResults());
        }

        log.info("Found active companies: verificationId={}, query={}, resultCount={}",
                verificationId, query, companiesFound.size());

        Company firstCompanyResult = companiesFound.getFirst();
        List<CompanyResponse> otherResults = companiesFound
                .subList(1, companiesFound.size())
                .stream()
                .map(CompanyResponse::from)
                .toList();

        return new BackendResponse(
                verificationId,
                query,
                SearchResult.found(
                        CompanyResponse.from(firstCompanyResult),
                        otherResults));
    }

}
