package com.marcos.incode_home_task.service;

import com.marcos.incode_home_task.company.Company;
import com.marcos.incode_home_task.dto.BackendResponse;
import com.marcos.incode_home_task.dto.CompanyResponse;
import com.marcos.incode_home_task.dto.SearchResult;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class BackendService
{
    private final ThirdPartyRestClient thirdPartyRestClient;

    public BackendService(ThirdPartyRestClient thirdPartyRestClient)
    {
        this.thirdPartyRestClient = thirdPartyRestClient;
    }

    public BackendResponse search(UUID verificationId, String query)
    {
        List<Company> companiesFound = thirdPartyRestClient.findFreeResults(query);

        if (companiesFound.isEmpty())
        {
            companiesFound = thirdPartyRestClient.findPremiumResults(query);
        }

        List<Company> activeCompaniesFound = companiesFound
                .stream()
                .filter(Company::active)
                .toList();

        if (activeCompaniesFound.isEmpty())
        {
            return new BackendResponse(
                    verificationId,
                    query,
                    SearchResult.noResults());
        }

        Company firstCompanyResult = activeCompaniesFound.getFirst();
        List<CompanyResponse> otherResults = activeCompaniesFound
                .subList(1, activeCompaniesFound.size())
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
