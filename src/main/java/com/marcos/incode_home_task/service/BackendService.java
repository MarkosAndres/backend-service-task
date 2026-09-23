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
    private final ThirdPartyService thirdPartyService;

    public BackendService(ThirdPartyService thirdPartyService)
    {
        this.thirdPartyService = thirdPartyService;
    }

    public BackendResponse search(UUID verificationId, String query)
    {
        List<Company> companiesFound = findCompanies(query);

        if (companiesFound.isEmpty())
        {
            return new BackendResponse(
                    verificationId,
                    query,
                    SearchResult.noResults());
        }

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

    private List<Company> findCompanies(String query)
    {
        List<Company> freeResults = thirdPartyService.findFreeResults(query);

        List<Company> companiesFound = freeResults.isEmpty()
                ? thirdPartyService.findPremiumResults(query)
                : freeResults;

        return companiesFound.stream()
                .filter(Company::active)
                .toList();
    }

}
